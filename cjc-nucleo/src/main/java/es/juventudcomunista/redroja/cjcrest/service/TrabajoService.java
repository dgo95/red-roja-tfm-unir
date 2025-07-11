package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.entity.*;
import es.juventudcomunista.redroja.cjcrest.repository.*;
import es.juventudcomunista.redroja.cjcrest.exception.ActividadEconomicaNoEncontradaException;
import es.juventudcomunista.redroja.cjcrest.exception.ModalidadTrabajoNoEncontradaException;
import es.juventudcomunista.redroja.cjcrest.exception.TipoContratoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class TrabajoService {

    @Autowired
    ActividadEconomicaRepository actividadEconomicaRepository;

    @Autowired
    TipoContratoRepository tipoContratoRepository;

    @Autowired
    ModalidadTrabajoRepository modalidadTrabajoRepository;

    @Autowired
    ContratoRepository contratoRepository;
    
    @Autowired
    CentroTrabajoRepository centroTrabajoRepository;
    
    @Autowired
    DireccionService direccionService;
    
    @Autowired
    EmpresaRepository empresaRepository;

    public List<ActividadEconomica> obtenerTodasLasActividadesEconomicas() {
        return actividadEconomicaRepository.findAll();
    }

    public List<TipoContrato> obtenerTodosTiposContrato() {
        return tipoContratoRepository.findAll();
    }

    public List<ModalidadTrabajo> obtenerTodasModalidadesTrabajo() {
        return modalidadTrabajoRepository.findAll();
    }

    // Devuelve el contrato del militante si existe, y si no crea uno nuevo y lo
    // devuele
    public Contrato obtenerContrato(Militante militante) {
        log.debug("obtenerContrato: Entrada al método");
        log.trace("obtenerContrato: Parámetro militante: {}",militante);
        var c = contratoRepository.findByMilitante(militante).orElseGet(() -> {
            return Contrato.builder()
                    .militante(militante)
                    .build();
        });
        log.trace("obtenerContrato: Contrato devuelto: {}",c);
        log.debug("obtenerContrato: Salida del método");
        return c;
        
    }

    public ActividadEconomica obtenerActividadEconomica(Integer actividadEconomica) {
        return actividadEconomicaRepository.findById(actividadEconomica)
                .orElseThrow(() -> new ActividadEconomicaNoEncontradaException(
                        "Actividad Economica no encontrada con id: " + actividadEconomica));
    }

    // Método para obtener el TipoContrato
    public TipoContrato obtenerTipoContrato(Integer tipoContrato) {
        return tipoContratoRepository.findById(tipoContrato)
                .orElseThrow(() -> new TipoContratoNoEncontradoException(
                        "Tipo de Contrato no encontrado con id: " + tipoContrato));
    }

    // Método para obtener la ModalidadTrabajo
    public ModalidadTrabajo obtenerModalidadTrabajo(Integer modalidadTrabajo) {
        return modalidadTrabajoRepository.findById(modalidadTrabajo)
                .orElseThrow(() -> new ModalidadTrabajoNoEncontradaException(
                        "Modalidad de Trabajo no encontrada con id: " + modalidadTrabajo));
    }

    public Direccion contruyeDireccion(Integer municipio, String direccionCentroTrabajo) {
        if(municipio ==null || municipio ==0 || direccionCentroTrabajo == null || direccionCentroTrabajo.isBlank()) {
            return null;
        }
        var d = new Direccion();
        d.setDireccion(direccionCentroTrabajo);
        d.setMunicipio(direccionService.getMunicipioById(municipio));
        return direccionService.guardar(d);
    }

    public Contrato guardaContrato(Contrato contrato) {
       return contratoRepository.save(contrato);
        
    }

    public void borraContrato(Militante m) {
        contratoRepository.deleteByMilitante(m);
        
    }

}
