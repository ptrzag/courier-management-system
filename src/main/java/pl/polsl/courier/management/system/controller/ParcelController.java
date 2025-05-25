package pl.polsl.courier.management.system.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.polsl.courier.management.system.entity.Parcel;
import pl.polsl.courier.management.system.entity.Client;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.repository.ParcelRepository;
import pl.polsl.courier.management.system.repository.ClientRepository;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

@RestController
@RequestMapping("/parcel")
public class ParcelController {

    @Autowired
    private ParcelRepository parcelRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private RoutePlanRepository routePlanRepo;

    /**
     * Create a new Parcel, linking it to an existing Client and RoutePlan.
     * @param parcel        the parcel data (without client/routePlan)
     * @param clientId      ID of the existing Client
     * @param routePlanId   ID of the existing RoutePlan
     */
    @PostMapping
    public ResponseEntity<String> addParcel(
            @RequestBody Parcel parcel,
            @RequestParam Long clientId,
            @RequestParam Long routePlanId
    ) {
        Optional<Client> clientOpt = clientRepo.findById(clientId);
        if (!clientOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Client with id=" + clientId + " not found");
        }

        Optional<RoutePlan> routeOpt = routePlanRepo.findById(routePlanId);
        if (!routeOpt.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("RoutePlan with id=" + routePlanId + " not found");
        }

        parcel.setClient(clientOpt.get());
        parcel.setRoutePlan(routeOpt.get());
        Parcel saved = parcelRepo.save(parcel);
        return ResponseEntity.ok("Added with id = " + saved.getId());
    }

    /**
     * Get one Parcel by its ID.
     * @param id  the parcel's ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Parcel> getParcel(@PathVariable Long id) {
        return parcelRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all Parcels.
     */
    @GetMapping
    public ResponseEntity<List<Parcel>> getAllParcels() {
        List<Parcel> list = StreamSupport
                .stream(parcelRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * Update an existing Parcel. You can also reassign Client or RoutePlan.
     * @param id            the ID of the parcel to update
     * @param details       the new parcel data
     * @param clientId      (optional) new Client ID
     * @param routePlanId   (optional) new RoutePlan ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateParcel(
            @PathVariable Long id,
            @RequestBody Parcel details,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long routePlanId
    ) {
        Optional<Parcel> existingOpt = parcelRepo.findById(id);
        if (!existingOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parcel not found");
        }

        Parcel parcel = existingOpt.get();
        parcel.setContentDescription(details.getContentDescription());
        parcel.setSenderAddress(details.getSenderAddress());
        parcel.setRecipientAddress(details.getRecipientAddress());
        parcel.setDispatchDate(details.getDispatchDate());
        parcel.setDeliveryDate(details.getDeliveryDate());
        parcel.setWeight(details.getWeight());
        parcel.setPrice(details.getPrice());

        if (clientId != null) {
            Optional<Client> clientOpt = clientRepo.findById(clientId);
            if (!clientOpt.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Client with id=" + clientId + " not found");
            }
            parcel.setClient(clientOpt.get());
        }

        if (routePlanId != null) {
            Optional<RoutePlan> routeOpt = routePlanRepo.findById(routePlanId);
            if (!routeOpt.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("RoutePlan with id=" + routePlanId + " not found");
            }
            parcel.setRoutePlan(routeOpt.get());
        }

        parcelRepo.save(parcel);
        return ResponseEntity.ok("Parcel updated");
    }

    /**
     * Delete a Parcel by its ID.
     * @param id  the parcel's ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        if (!parcelRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        parcelRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}