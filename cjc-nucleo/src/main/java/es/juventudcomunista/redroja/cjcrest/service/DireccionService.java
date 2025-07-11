package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.entity.ComunidadAutonoma;
import es.juventudcomunista.redroja.cjcrest.entity.Direccion;
import es.juventudcomunista.redroja.cjcrest.entity.Municipio;
import es.juventudcomunista.redroja.cjcrest.entity.Provincia;
import es.juventudcomunista.redroja.cjcrest.mapper.MunicipioMapper;
import es.juventudcomunista.redroja.cjcrest.mapper.ProvinciaMapper;
import es.juventudcomunista.redroja.cjcrest.repository.ComunidadAutonomaRepository;
import es.juventudcomunista.redroja.cjcrest.repository.DireccionRepository;
import es.juventudcomunista.redroja.cjcrest.repository.MunicipioRepository;
import es.juventudcomunista.redroja.cjcrest.repository.ProvinciaRepository;
import es.juventudcomunista.redroja.cjcrest.web.dto.MunicipioDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ProvinciaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DireccionService {


    private final ComunidadAutonomaRepository comunidadAutonomaRepository;


    private final ProvinciaRepository provinciaRepository;


    private final MunicipioRepository municipioRepository;


    private final DireccionRepository direccionRepository;

    public List<ComunidadAutonoma> obtenerTodasLasComunidadesAutonomas() {
        return comunidadAutonomaRepository.findAll();
    }

    public List<Provincia> obtenerProvinciasPorComunidad(ComunidadAutonoma comunidadAutonoma) {
        return provinciaRepository.findByComunidadAutonoma(comunidadAutonoma);
    }

    public List<Municipio> obtenerMunicipiosPorProvincia(Provincia provinciaId) {
        return municipioRepository.findByProvincia(provinciaId);
    }

    public List<ProvinciaDTO> obtenerProvinciasPorComunidadId(int id) {
        ComunidadAutonoma c = comunidadAutonomaRepository.findById(id).orElseThrow();
        return ProvinciaMapper.INSTANCE.toDTOs(obtenerProvinciasPorComunidad(c));
    }

    public List<MunicipioDTO> obtenerMunicipiosPorProvinciaId(int idProvincia) {
        Provincia p = provinciaRepository.findById(idProvincia).orElseThrow();
        return MunicipioMapper.INSTANCE.toDTOs(obtenerMunicipiosPorProvincia(p));
    }

    public Municipio getMunicipioById(int municipio) {
        
        return municipioRepository.findById(municipio).orElse(null);
    }

    public Direccion guardar(Direccion d) {
        return direccionRepository.save(d);
        
    }

}
