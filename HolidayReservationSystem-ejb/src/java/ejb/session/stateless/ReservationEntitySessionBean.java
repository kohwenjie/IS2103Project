/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GuestEntity;
import entity.PartnerEntity;
import entity.ReservationEntity;
import entity.RoomEntity;
import entity.RoomRateEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomRateTypeEnum;
import util.enumeration.RoomStatusEnum;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.InsufficientRoomsAvailableException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mingy
 */
@Stateless
public class ReservationEntitySessionBean implements ReservationEntitySessionBeanRemote, ReservationEntitySessionBeanLocal {

    @EJB
    private RoomTypeEntitySessionBeanLocal roomTypeEntitySessionBeanLocal;

    @EJB
    private RoomRateEntitySessionBeanLocal roomRateEntitySessionBeanLocal;
    @Resource
    private EJBContext eJBContext;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public ReservationEntitySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewReservation(ReservationEntity newReservation, List<String> listOfRoomRateNames) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservation);

        LocalDateTime startDate = newReservation.getReservationStartDate();
        LocalDateTime endDate = newReservation.getReservationEndDate();
        try {
            HashMap<RoomTypeEntity, HashMap<String, BigDecimal>> map = retrieveRoomTypeAvailabilities(startDate, endDate, 1, true);
            RoomTypeEntity roomType = roomTypeEntitySessionBeanLocal.retrieveRoomTypeByName(newReservation.getRoomTypeName());
            if (map.get(roomType).get("numRoomType").equals(BigDecimal.ZERO)) {
                throw new CreateNewReservationException();
            } else {
                if (constraintViolations.isEmpty()) {
                    try {
                        for (String roomRateName : listOfRoomRateNames) {
                            RoomRateEntity roomRate;
                            try {
                                roomRate = roomRateEntitySessionBeanLocal.retrieveRoomRateByName(roomRateName);
                                if (!newReservation.getRoomRateEntities().contains(roomRate)) {
                                    newReservation.getRoomRateEntities().add(roomRate);
                                }
                            } catch (RoomRateNotFoundException ex) {
                                Logger.getLogger(ReservationEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        em.persist(newReservation);
                        em.flush();

                        return newReservation.getReservationEntityId();
                    } catch (PersistenceException ex) {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            }
        } catch (InsufficientRoomsAvailableException | RoomTypeNotFoundException ex) {
            throw new CreateNewReservationException();
        }

    }

    @Override
    public Long createNewReservationForGuest(ReservationEntity newReservation, List<String> listOfRoomRateNames, GuestEntity guest) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservation);

        LocalDateTime startDate = newReservation.getReservationStartDate();
        LocalDateTime endDate = newReservation.getReservationEndDate();
        try {
            HashMap<RoomTypeEntity, HashMap<String, BigDecimal>> map = retrieveRoomTypeAvailabilities(startDate, endDate, 1, true);
            RoomTypeEntity roomType = roomTypeEntitySessionBeanLocal.retrieveRoomTypeByName(newReservation.getRoomTypeName());
            if (map.get(roomType).get("numRoomType").equals(BigDecimal.ZERO)) {
                throw new CreateNewReservationException();
            } else {
                if (constraintViolations.isEmpty()) {
                    try {
                        for (String roomRateName : listOfRoomRateNames) {
                            RoomRateEntity roomRate;
                            try {
                                roomRate = roomRateEntitySessionBeanLocal.retrieveRoomRateByName(roomRateName);
                                if (!newReservation.getRoomRateEntities().contains(roomRate)) {
                                    newReservation.getRoomRateEntities().add(roomRate);
                                }
                            } catch (RoomRateNotFoundException ex) {
                                Logger.getLogger(ReservationEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        GuestEntity guestEntity = em.find(GuestEntity.class, guest.getUserEntityId());
                        guestEntity.getReservationEntities().add(newReservation);

                        em.persist(newReservation);
                        em.flush();

                        return newReservation.getReservationEntityId();
                    } catch (PersistenceException ex) {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            }
        } catch (InsufficientRoomsAvailableException | RoomTypeNotFoundException ex) {
            throw new CreateNewReservationException();
        }
    }

    @Override
    public Long createNewReservationForPartner(ReservationEntity newReservation, List<String> listOfRoomRateNames, PartnerEntity partner) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(newReservation);

        LocalDateTime startDate = newReservation.getReservationStartDate();
        LocalDateTime endDate = newReservation.getReservationEndDate();
        try {
            HashMap<RoomTypeEntity, HashMap<String, BigDecimal>> map = retrieveRoomTypeAvailabilities(startDate, endDate, 1, true);
            RoomTypeEntity roomType = roomTypeEntitySessionBeanLocal.retrieveRoomTypeByName(newReservation.getRoomTypeName());
            if (map.get(roomType).get("numRoomType").equals(BigDecimal.ZERO)) {
                throw new CreateNewReservationException();
            } else {
                if (constraintViolations.isEmpty()) {
                    try {
                        System.out.println(listOfRoomRateNames.toString());
                        for (String roomRateName : listOfRoomRateNames) {
                            RoomRateEntity roomRate;
                            try {
                                roomRate = roomRateEntitySessionBeanLocal.retrieveRoomRateByName(roomRateName);
                                if (!newReservation.getRoomRateEntities().contains(roomRate)) {
                                    newReservation.getRoomRateEntities().add(roomRate);
                                }
                            } catch (RoomRateNotFoundException ex) {
                                Logger.getLogger(ReservationEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        PartnerEntity partnerEntity = em.find(PartnerEntity.class, partner.getUserEntityId());
                        partnerEntity.getReservationEntities().add(newReservation);

                        em.persist(newReservation);
                        em.flush();

                        return newReservation.getReservationEntityId();
                    } catch (PersistenceException ex) {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            }
        } catch (InsufficientRoomsAvailableException | RoomTypeNotFoundException ex) {
            throw new CreateNewReservationException();
        }
    }

    @Override
    public void createNewReservations(List<Pair<ReservationEntity, List<String>>> list) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {

        for (Pair<ReservationEntity, List<String>> pair : list) {
            try {
                createNewReservation(pair.getKey(), pair.getValue());
            } catch (CreateNewReservationException ex) {
                eJBContext.setRollbackOnly();
                throw new CreateNewReservationException();
            }
        }
    }

    @Override
    public void createNewReservationsForGuest(List<Pair<ReservationEntity, List<String>>> list, GuestEntity guest) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {
        for (Pair<ReservationEntity, List<String>> pair : list) {
            try {
                createNewReservationForGuest(pair.getKey(), pair.getValue(), guest);
            } catch (CreateNewReservationException ex) {
                eJBContext.setRollbackOnly();
                throw new CreateNewReservationException();
            }
        }
    }

    @Override
    public void createNewReservationsForPartner(List<Pair<ReservationEntity, List<String>>> list, PartnerEntity partner) throws CreateNewReservationException, UnknownPersistenceException, InputDataValidationException {
        for (Pair<ReservationEntity, List<String>> pair : list) {
            try {
                createNewReservationForPartner(pair.getKey(), pair.getValue(), partner);
            } catch (CreateNewReservationException ex) {
                eJBContext.setRollbackOnly();
                throw new CreateNewReservationException();
            }
        }
    }

    @Override
    public List<ReservationEntity> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r");

        List<ReservationEntity> listReservations = query.getResultList();
        for (ReservationEntity reservationEntity : listReservations) {
            reservationEntity.getRoomEntity();
            reservationEntity.getRoomRateEntities().size();
        }

        return listReservations;
    }

    @Override
    public ReservationEntity retrieveReservationById(Long reservationId) throws ReservationNotFoundException {

        ReservationEntity reservation = em.find(ReservationEntity.class, reservationId);
        if (reservation != null) {
            if (reservation.getRoomEntity() != null) {
                reservation.getRoomEntity();
                reservation.getRoomRateEntities().size();
            }
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation Id: " + reservationId + " does not exist");
        }
    }

    @Override
    public List<ReservationEntity> retrieveReservationByPassportNumber(String passportNumber) {

        Query query = em.createQuery(
                "SELECT r FROM ReservationEntity r WHERE r.passportNumber = :passportNum")
                .setParameter("passportNum", passportNumber);

        List<ReservationEntity> reservations = query.getResultList();

        for (ReservationEntity reservation : reservations) {
            reservation.getRoomEntity();
            reservation.getRoomRateEntities().size();
        }
        return reservations;
    }

    @Override
    public List<ReservationEntity> retrieveReservationByPassportNumberForCheckIn(String passportNumber) {
        LocalDateTime checkInDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Query query = em.createQuery(
                "SELECT r FROM ReservationEntity r WHERE r.passportNumber = :passportNum AND r.reservationStartDate = :inDate")
                .setParameter("passportNum", passportNumber)
                .setParameter("inDate", checkInDate);

        List<ReservationEntity> reservations = query.getResultList();

        for (ReservationEntity reservation : reservations) {
            reservation.getRoomEntity();
            reservation.getRoomRateEntities().size();
        }
        return reservations;
    }

    @Override
    public List<ReservationEntity> retrieveReservationByPassportNumberForCheckOut(String passportNumber) {
        LocalDateTime checkInDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Query query = em.createQuery(
                "SELECT r FROM ReservationEntity r WHERE r.passportNumber = :passportNum AND r.reservationEndDate = :inDate")
                .setParameter("passportNum", passportNumber)
                .setParameter("inDate", checkInDate);

        List<ReservationEntity> reservations = query.getResultList();

        for (ReservationEntity reservation : reservations) {
            reservation.getRoomEntity();
            reservation.getRoomRateEntities().size();
        }
        return reservations;
    }

    @Override
    public List<ReservationEntity> retrieveAllReservationsWithStartDate(LocalDateTime startDate
    ) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.reservationStartDate = :inDate").setParameter("inDate", startDate);

        List<ReservationEntity> reservations = query.getResultList();

        for (ReservationEntity reservation : reservations) {
            reservation.getRoomEntity();
            reservation.getRoomRateEntities().size();
        }

        return reservations;

    }

    @Override
    public List<ReservationEntity> retrieveAllReservationsWithEndDate(LocalDateTime endDate) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.reservationEndDate = :inDate").setParameter("inDate", endDate);

        List<ReservationEntity> reservations = query.getResultList();

        for (ReservationEntity reservation : reservations) {
            reservation.getRoomEntity();
            reservation.getRoomRateEntities().size();
        }

        return reservations;

    }

    @Override
    public HashMap<RoomTypeEntity, HashMap<String, BigDecimal>> retrieveRoomTypeAvailabilities(LocalDateTime startDate, LocalDateTime endDate, Integer numRooms, Boolean isWalkIn) throws InsufficientRoomsAvailableException {
        HashMap<RoomTypeEntity, HashMap<String, BigDecimal>> map = new HashMap<>();
        HashMap<RoomTypeEntity, Integer> highestNumOfInventoryUsedPerRoomType = new HashMap<>();
        int totalInventoryRooms = 0;

        //get list of roomtypes that are not disabled
        List<RoomTypeEntity> listOfAllRoomTypes = roomTypeEntitySessionBeanLocal.retrieveAllRoomTypes();
        List<RoomTypeEntity> listOfAvailableRoomTypes = new ArrayList<>();
        for (RoomTypeEntity roomType : listOfAllRoomTypes) {
            if (roomType.getIsDisabled() == false) {
                listOfAvailableRoomTypes.add(roomType);
            }
        }
        //HERE
        //get number of rooms of each room type that is not disabled (INVENTORY)
        for (RoomTypeEntity roomType : listOfAvailableRoomTypes) {
            HashMap<String, BigDecimal> stringToBigDecimalMap = new HashMap<>();
            List<RoomEntity> listOfRooms = roomType.getRoomEntities();

            //check if there is a reservation that is checked in for the room, which means that the room has people staying in
            int numInventory = 0;
            for (RoomEntity room : listOfRooms) {
                if (room.getRoomStatusEnum() == RoomStatusEnum.AVAILABLE) {
                    numInventory += 1;
                }
            }
            //get best price according to whether this is walk in or online
            Pair<BigDecimal, List<RoomRateEntity>> pair;
            if (isWalkIn) {
                pair = getPublishedPrice(startDate, endDate, roomType);
            } else {
                pair = getOnlinePrice(startDate, endDate, roomType);
            }

            stringToBigDecimalMap.put("bestPrice", pair.getKey());
            stringToBigDecimalMap.put("numRoomType", BigDecimal.valueOf(numInventory));
            for (RoomRateEntity roomRate : pair.getValue()) {
                stringToBigDecimalMap.put(roomRate.getRoomRateName(), BigDecimal.ZERO);
            }
            map.put(roomType, stringToBigDecimalMap);
            highestNumOfInventoryUsedPerRoomType.put(roomType, 0);
        }

        //checking for the number of reservations for each roomTypes there is per day, and store the numbers
        LocalDateTime currDate = startDate;
        while (currDate.isBefore(endDate)) {
            Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.reservationStartDate <= :inDate AND r.reservationEndDate > :inDate").setParameter("inDate", currDate);
            List<ReservationEntity> listOfReservations = query.getResultList();
            HashMap<RoomTypeEntity, Integer> numRoomUsedPerRoomType = new HashMap<>();
            if (!listOfReservations.isEmpty()) {
                for (ReservationEntity res : listOfReservations) {
                    RoomTypeEntity roomType = res.getRoomRateEntities().get(0).getRoomTypeEntity();
                    if (numRoomUsedPerRoomType.containsKey(roomType)) {
                        numRoomUsedPerRoomType.put(roomType, numRoomUsedPerRoomType.get(roomType) + 1);
                    } else {
                        numRoomUsedPerRoomType.put(roomType, 1);
                    }
                }
            }
            //compare to the map that stores the highest, so that later the inventory can be deducted by the highest number of rooms used per room type
            List<RoomTypeEntity> listOfRoomTypeEntities = new ArrayList<>(numRoomUsedPerRoomType.keySet());
            for (RoomTypeEntity roomType : listOfRoomTypeEntities) {
                if (highestNumOfInventoryUsedPerRoomType.containsKey(roomType)) {
                    if (highestNumOfInventoryUsedPerRoomType.get(roomType) < numRoomUsedPerRoomType.get(roomType)) {
                        highestNumOfInventoryUsedPerRoomType.put(roomType, numRoomUsedPerRoomType.get(roomType));
                    }
                }

            }
            currDate = currDate.plusDays(1);
        }

        List<RoomTypeEntity> listOfHighestUsedInventoryForRoomType = new ArrayList<>(highestNumOfInventoryUsedPerRoomType.keySet());
        for (RoomTypeEntity roomType : listOfHighestUsedInventoryForRoomType) {
            if (map.containsKey(roomType)) {
                HashMap<String, BigDecimal> stringToBigDecimalMap = map.get(roomType);
                BigDecimal numRoomsLeft = stringToBigDecimalMap.get("numRoomType").subtract(BigDecimal.valueOf(highestNumOfInventoryUsedPerRoomType.get(roomType)));
                if (numRoomsLeft.compareTo(BigDecimal.ZERO) <= 0) {
                    numRoomsLeft = BigDecimal.ZERO;
                }
                stringToBigDecimalMap.put("numRoomType", numRoomsLeft);
                totalInventoryRooms += numRoomsLeft.intValue();
                map.put(roomType, stringToBigDecimalMap);
            }
        }

        if (totalInventoryRooms < numRooms) {
            throw new InsufficientRoomsAvailableException();
        }

        return map;
    }

    //Have a new calculate method that returns Pair of finalBestPrice and list of roomRates used
    //assume that there can be more than 1 published rate that is not disabled AND valid, and you will take the lower price
    private Pair<BigDecimal, List<RoomRateEntity>> getPublishedPrice(LocalDateTime startDate, LocalDateTime endDate, RoomTypeEntity roomType) {
        LocalDateTime currDate = startDate;
        List<RoomRateEntity> listRoomRateEntity = new ArrayList<>();
        BigDecimal totalBestPrice = BigDecimal.ZERO;
        while (currDate.isBefore(endDate)) {
            List<RoomRateEntity> listOfRoomRate = roomType.getRoomRateEntities();
            RoomRateEntity bestPublishedPrice = null;
            BigDecimal lowestPrice = BigDecimal.valueOf(9999);
            for (RoomRateEntity roomRate : listOfRoomRate) {
                if (roomRate.getIsDisabled() == false
                        && (currDate.isEqual(roomRate.getValidPeriodFrom()) || currDate.isAfter(roomRate.getValidPeriodFrom()))
                        && (currDate.isEqual(roomRate.getValidPeriodTo()) || currDate.isBefore(roomRate.getValidPeriodTo()))) {

                    if (roomRate.getRoomRateTypeEnum() == RoomRateTypeEnum.PUBLISHED) {
                        if (roomRate.getRatePerNight().compareTo(lowestPrice) < 0) {
                            bestPublishedPrice = roomRate;
                            lowestPrice = roomRate.getRatePerNight();
                        }

                    }
                }
            }
            listRoomRateEntity.add(bestPublishedPrice);
            totalBestPrice = totalBestPrice.add(bestPublishedPrice.getRatePerNight());
            currDate = currDate.plusDays(1);
        }

        return new Pair(totalBestPrice, listRoomRateEntity);
    }
    
    //Have a new calculate method that returns Pair of finalBestPrice and list of roomRates used
    //Will use Normal rate if there are no Peak or Promo, else if have promo and peak use promo, else peak
    private Pair<BigDecimal, List<RoomRateEntity>> getOnlinePrice(LocalDateTime startDate, LocalDateTime endDate, RoomTypeEntity roomType) {
        LocalDateTime currDate = startDate;
        List<RoomRateEntity> listRoomRateEntity = new ArrayList<>();
        BigDecimal totalBestPrice = BigDecimal.ZERO;
        while (currDate.isBefore(endDate)) {
            List<RoomRateEntity> listOfRoomRate = roomType.getRoomRateEntities();
            RoomRateEntity normalRate = null;
            BigDecimal normalPrice = BigDecimal.valueOf(9999);
            RoomRateEntity peakRate = null;
            BigDecimal peakPrice = BigDecimal.valueOf(9999);
            RoomRateEntity promoRate = null;
            BigDecimal promoPrice = BigDecimal.valueOf(9999);

            for (RoomRateEntity roomRate : listOfRoomRate) {
                if (roomRate.getIsDisabled() == false
                        && (currDate.isEqual(roomRate.getValidPeriodFrom()) || currDate.isAfter(roomRate.getValidPeriodFrom()))
                        && (currDate.isEqual(roomRate.getValidPeriodTo()) || currDate.isBefore(roomRate.getValidPeriodTo()))) {
                    if (roomRate.getRoomRateTypeEnum() != RoomRateTypeEnum.PUBLISHED) {
                        if (roomRate.getRoomRateTypeEnum() == RoomRateTypeEnum.NORMAL) {
                            if (roomRate.getRatePerNight().compareTo(normalPrice) < 0) {
                                normalRate = roomRate;
                                normalPrice = roomRate.getRatePerNight();
                            }
                        } else if (roomRate.getRoomRateTypeEnum() == RoomRateTypeEnum.PEAK) {
                            if (roomRate.getRatePerNight().compareTo(peakPrice) < 0) {
                                peakRate = roomRate;
                                peakPrice = roomRate.getRatePerNight();
                            }
                        } else {
                            if (roomRate.getRatePerNight().compareTo(promoPrice) < 0) {
                                promoRate = roomRate;
                                promoPrice = roomRate.getRatePerNight();
                            }
                        }
                    }
                }
            }

            if (promoRate != null) {
                listRoomRateEntity.add(promoRate);
                totalBestPrice = totalBestPrice.add(promoRate.getRatePerNight());
            } else if (peakRate != null) {
                listRoomRateEntity.add(peakRate);
                totalBestPrice = totalBestPrice.add(peakRate.getRatePerNight());
            } else {
                listRoomRateEntity.add(normalRate);
                totalBestPrice = totalBestPrice.add(normalRate.getRatePerNight());
            }

            currDate = currDate.plusDays(1);
        }

        return new Pair(totalBestPrice, listRoomRateEntity);
    }
    
    @Override
    public void setReservationToCheckedIn(ReservationEntity reservationEntity) {
        ReservationEntity res = em.find(ReservationEntity.class, reservationEntity.getReservationEntityId());
        res.setIsCheckedIn(true);
    }
    
    @Override
    public void setReservationToCheckedOut(ReservationEntity reservationEntity) {
        ReservationEntity res = em.find(ReservationEntity.class, reservationEntity.getReservationEntityId());
        res.setIsCheckedIn(false);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
