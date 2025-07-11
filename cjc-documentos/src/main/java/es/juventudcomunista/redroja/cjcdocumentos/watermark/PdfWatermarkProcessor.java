package es.juventudcomunista.redroja.cjcdocumentos.watermark;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.google.zxing.*;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
public class PdfWatermarkProcessor implements WatermarkProcessor {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private static final float QR_TARGET_PX = 48f;   // anchura/altura objetivo
    private static final float QR_X_MARGIN  = 36f;   // distancia al borde izquierdo
    private static final float QR_Y_MARGIN  = 15f;   // distancia al borde inferior
    private static final float TEXT_Y_MARGIN = 15f;  // mismo eje vertical que el QR


    @Override
    public boolean supports(String mime) {
        return "application/pdf".equalsIgnoreCase(mime);
    }

    @Override
    public void apply(InputStream in, OutputStream out, WatermarkInfo info) throws Exception {
        try (PdfReader reader = new PdfReader(in);
             PdfStamper stamper = new PdfStamper(reader, out)) {

            /* ---------- datos dinámicos ---------- */
            String ts = ISO.format(info.downloadedAtUtc());
            String text = "%s @ %s".formatted(info.userId(), ts);
            String qrPayload = "%s|%s|%s".formatted(
                    info.documentId(), info.userId(), ts);

            /* ---------- QR: se calcula UNA vez ---------- */
            BufferedImage qrBuffered = generateQr(qrPayload);
            Image qrImg = Image.getInstance(qrBuffered, null);
            qrImg.scaleToFit(QR_TARGET_PX, QR_TARGET_PX);

            final float qrW = qrImg.getScaledWidth();
            final float qrH = qrImg.getScaledHeight();

            /* ---------- iteramos cada página ---------- */
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                PdfContentByte canvas = stamper.getOverContent(page);

                /* Coordenada X del texto = borde derecho - margen */
                Rectangle pageSize = reader.getPageSize(page);
                float textX = pageSize.getRight() - QR_X_MARGIN;

                /* 1️⃣ fondo blanco exacto al QR */
                canvas.saveState();
                canvas.setColorFill(Color.WHITE);
                canvas.rectangle(QR_X_MARGIN, QR_Y_MARGIN, qrW, qrH);
                canvas.fill();
                canvas.restoreState();

                /* 2️⃣ QR (con fondo transparente) */
                qrImg.setAbsolutePosition(QR_X_MARGIN, QR_Y_MARGIN);
                canvas.addImage(qrImg);

                /* 3️⃣ texto usuario@timestamp */
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_RIGHT,
                        new Phrase(text, font()),
                        textX, TEXT_Y_MARGIN,
                        0);
            }
        }
    }

    private static final MatrixToImageConfig TRANSPARENT_BG =
            new MatrixToImageConfig(
                    0xFF000000,   // negro opaco
                    0x00FFFFFF);

    private BufferedImage generateQr(String payload) throws Exception {
        var hints = Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.CHARACTER_SET,     "UTF-8",
                EncodeHintType.MARGIN, 0
        );

        BitMatrix matrix = new MultiFormatWriter()
                .encode(payload, BarcodeFormat.QR_CODE, 120, 120, hints);

        return MatrixToImageWriter.toBufferedImage(matrix, TRANSPARENT_BG);
    }

    private Font font() {
        Font f = new Font(Font.HELVETICA, 8);
        f.setColor(Color.GRAY);
        return f;
    }
}
