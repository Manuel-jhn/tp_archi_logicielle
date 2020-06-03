package fr.ensim.archi.archirest.controller;

import fr.ensim.archi.archirest.Base_de_donnees;
import fr.ensim.archi.archirest.model.Garantie;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class GarantieRestController {


    Base_de_donnees database = new Base_de_donnees();

    // Simulation d'un séquenceur pour générer l'identifiant des équipes
    private AtomicInteger fakeSeq = new AtomicInteger(0);


    @PostMapping("/api/garantie")
    public ResponseEntity<Garantie> creerGarantie(@RequestParam("nom") String nom,@RequestParam("montant") float montant, @RequestParam("description") String description) {
        if (StringUtils.isBlank(nom)) {
            return ResponseEntity.status(400).build();
        }

        // affectation d'un id et persistance
        Garantie garantie = new Garantie();
        garantie.setId(fakeSeq.incrementAndGet());
        garantie.setNom(nom);
        garantie.setMontant(montant);
        garantie.setDescription(description);
        database.getDatabase().put(garantie.getId(), garantie);

        // URI de localisation de la ressource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(garantie.getId());

        // réponse 201 avec la localisation et la ressource créée
        return ResponseEntity.created(location).body(garantie);
    }

    @GetMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> getGarantie(@PathVariable("id") @NotNull int id) {
        if (database.getDatabase().containsKey(id)) {
            return ResponseEntity.ok(database.getDatabase().get(id));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("api/garantie")
    public ResponseEntity<Collection<Garantie>> getGaranties() {
        return ResponseEntity.ok().body(database.getDatabase().values());
    }

    @DeleteMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> deleteGarantie(@PathVariable("id") @NotNull int id) {
        if (database.getDatabase().containsKey(id)) {
            database.getDatabase().remove(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> putGarantie(@PathVariable("id") @NotNull int id, @RequestBody @Valid Garantie garantie) {
        if (database.getDatabase().containsKey(id)) {
            // si garantie existante, mise à jour
            garantie.setId(id);
            database.getDatabase().put(id, garantie);

            return ResponseEntity.ok().build();
        } else {
            // si garantie inexistante, création

            // affectation d'un id et persistance
            garantie.setId(fakeSeq.incrementAndGet());
            database.getDatabase().put(garantie.getId(), garantie);

            // URI de localisation de la ressource
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build(garantie.getId());

            // réponse 202 avec la localisation et la ressource créée
            return ResponseEntity.created(location).build();
        }
    }
}
