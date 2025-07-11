package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.entity.Federacion;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.Sindicacion;
import es.juventudcomunista.redroja.cjcrest.entity.Sindicato;
import es.juventudcomunista.redroja.cjcrest.repository.FederacionRepository;
import es.juventudcomunista.redroja.cjcrest.repository.SindicacionRepository;
import es.juventudcomunista.redroja.cjcrest.repository.SindicatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SindicatoService {
	@Autowired
	SindicatoRepository sindicatoRepository;
	
	@Autowired
	SindicacionRepository sindicacionRepository;
	
	@Autowired
	FederacionRepository federacionRepository;
	
	public void guardar(Sindicato sindicato) {
		sindicatoRepository.save(sindicato);
	}
	public boolean existe(String string) {
		
		return sindicatoRepository.existsByNombre(string);
	}
	public Sindicato getSindicatoByNombre(String nombre) {
		var aux=sindicatoRepository.findByNombre(nombre);
		if(aux.isPresent()) {
			return aux.get();
		}
		return null;
	}
	public List<Sindicato> getAllSindicatos() {
	    return sindicatoRepository.findAll().stream()
	                               .filter(sindicato -> sindicato.getId() > 0)
	                               .collect(Collectors.toList());
	}
    public Sindicacion obtenerMilitancia(Militante militante) {
        
        return sindicacionRepository.findByMilitante(militante).orElseGet(() -> {
            var s = Sindicato.builder().id(0).build();
            var f = Federacion.builder().id(0).sindicato(s).build();
            return Sindicacion.builder()
                    .militante(militante)
                    .participaAreaJuventud(false)
                    .federacion(f)
                    .build();
        });
    }
    public List<Federacion> getAllFederacionesBySindicato(Sindicato sindicato) {
        return federacionRepository.findBySindicato(sindicato);
    }
    public List<Federacion> getAllFederacionesBySindicato(int idSindicato) {
        Sindicato s = sindicatoRepository.findById(idSindicato).orElseGet(() -> {
            return Sindicato.builder()
                    .id(0)
                    .build();
        });
        return getAllFederacionesBySindicato(s);
    }
    public Federacion getFederacionById(Integer federacion) {
        
        return federacionRepository.findById(federacion).orElseThrow();
    }
    public Sindicacion actualizaSindicacion(Sindicacion s) {
        return sindicacionRepository.save(s);
        
    }
    public Sindicato getSindicatoById(Integer sindicato) {
        return sindicatoRepository.findById(sindicato).orElseThrow();
    }
    public void borraSindicacion(Militante m) {
       sindicacionRepository.deleteByMilitante(m);
        
    }
}
