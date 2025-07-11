package es.juventudcomunista.redroja.cjcdocumentos.execption;

import java.net.MalformedURLException;

public class FotoPerfilException extends RuntimeException {
	public FotoPerfilException(String m, MalformedURLException e) {
		super(m,e);
	}

}
