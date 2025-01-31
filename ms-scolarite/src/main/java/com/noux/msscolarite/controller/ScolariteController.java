package com.noux.msscolarite.controller;

import com.noux.msscolarite.entities.Etudiant;
import com.noux.msscolarite.models.Formation;
import com.noux.msscolarite.proxies.BourseProxy;
import com.noux.msscolarite.proxies.FormationProxy;
import com.noux.msscolarite.repositories.EtudiantRepository;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class ScolariteController {

    @Resource
    EtudiantRepository etudiantRepository;
    @Resource
    FormationProxy formationProxy;

    @Resource
    BourseProxy bourseProxy;


   // http://localhost:8082/api/v1/etudiantV/{idE}
    @GetMapping("/etudiantV/{idE}")
    public ResponseEntity<?> getEtudiantWithVirements(@PathVariable("idE") Long idE) {
        Optional<Etudiant> etudiantOptional = etudiantRepository.findById(idE);
        if (etudiantOptional.isPresent()) {
            Etudiant etudiant = etudiantOptional.get();
            etudiant.setVirements(new ArrayList(bourseProxy.getVirements(idE,"toscolarite").getContent()));
            return ResponseEntity.ok(etudiant);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Etudiant not found with id " + idE);
    }

    // http://localhost:8082/api/v1/etudiantF/{idE}
    @GetMapping("/etudiantF/{idE}")
    public ResponseEntity<?> getEtudiantWithFormation(@PathVariable("idE") Long idE) {
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idE);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            Long idFormation = etudiant.getIdFormation();
            if (idFormation != null) {
                Formation formation = formationProxy.getFormation(idFormation);
                etudiant.setFormation(formation);
                return ResponseEntity.ok(etudiant);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Formation not found for Etudiant with id " + idE);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Etudiant not found with id " + idE);
    }

    // http://localhost:8082/api/v1/etudiants/{id}
    @GetMapping("/etudiants/{id}")
    public ResponseEntity<?> getEtudiantWithFormationBourse(@PathVariable("id") Long ide) {
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(ide);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            Formation formation = formationProxy.getFormation(etudiant.getIdFormation());
            etudiant.setFormation(formation);
            etudiant.setVirements(new ArrayList(bourseProxy.getVirements(ide, "toscolarite").getContent()));
            return ResponseEntity.ok(etudiant);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Etudiant not found with id " + ide);
    }

    // http://localhost:8082/api/v1/etudiants
    @GetMapping("/etudiants")
    public ResponseEntity<List<Etudiant>> getEtudiantsWithFormationBourse() {
        List<Etudiant> etudiants = etudiantRepository.findAll();

        etudiants.forEach((e) -> {
            e.setVirements(new ArrayList<>(bourseProxy.getVirements(e.getIdEtudiant(),
                    "toscolarite").getContent()));
            e.setFormation(formationProxy.getFormation(e.getIdFormation()));
        });

        return ResponseEntity.ok(etudiants);
    }

}
