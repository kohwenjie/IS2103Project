/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReportEntity;
import entity.ReservationEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.ExceptionReportTypeEnum;
import util.exception.ExceptionReportNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.NoExceptionReportFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mingy
 */
@Stateless
public class ExceptionReportEntitySessionBean implements ExceptionReportEntitySessionBeanRemote, ExceptionReportEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ExceptionReportEntitySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewExceptionReport(ExceptionReportEntity newExceptionReport) throws UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ExceptionReportEntity>> constraintViolations = validator.validate(newExceptionReport);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newExceptionReport);
                em.flush();

                return newExceptionReport.getExceptionReportId();
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<ExceptionReportEntity> retrieveAllExceptionReport() {
        Query query = em.createQuery("SELECT er FROM ExceptionReportEntity er");
        List<ExceptionReportEntity> listOfExceptionReportEntities = query.getResultList();
        for (ExceptionReportEntity exceptionReportEntity : listOfExceptionReportEntities) {
            exceptionReportEntity.getReservationEntity().getRoomEntity();
        }
        return listOfExceptionReportEntities;
    }

    @Override
    public ExceptionReportEntity retrieveExceptionReportById(Long exceptionReportId) throws ExceptionReportNotFoundException {
        ExceptionReportEntity exceptionReport = em.find(ExceptionReportEntity.class, exceptionReportId);

        if (exceptionReport != null) {
            exceptionReport.getReservationEntity();

            return exceptionReport;
        } else {
            throw new ExceptionReportNotFoundException("Exception Report ID " + exceptionReportId + " does not exist");
        }
    }

    @Override
    public List<ExceptionReportEntity> retrieveExceptionReportsByTypeAndDate(ExceptionReportTypeEnum exceptionReportTypeEnum, LocalDateTime date) {
        List<ExceptionReportEntity> listOfExceptionReportEntities = em.createQuery("SELECT er FROM ExceptionReportEntity er WHERE er.exceptionReportTypeEnum = :inExceptionReportType AND  er.generationDate = :inDate ")
                .setParameter("inExceptionReportType", exceptionReportTypeEnum)
                .setParameter("inDate", date)
                .getResultList();
        for (ExceptionReportEntity exceptionReportEntity : listOfExceptionReportEntities) {
            if (exceptionReportEntity.getReservationEntity().getRoomEntity() != null) {
                exceptionReportEntity.getReservationEntity().getRoomEntity().getRoomTypeEntity();
            }

        }
        return listOfExceptionReportEntities;
    }

    @Override
    public ExceptionReportEntity retrieveExceptionReportByReservation(ReservationEntity res) throws NoExceptionReportFoundException {
        Query query = em.createQuery("SELECT er FROM ExceptionReportEntity er WHERE er.reservationEntity.reservationEntityId = :inReservationEntity ")
                .setParameter("inReservationEntity", res.getReservationEntityId());
        ExceptionReportEntity exceptionReportEntity;
        try {
            exceptionReportEntity = (ExceptionReportEntity) query.getSingleResult();
            exceptionReportEntity.getReservationEntity();
        } catch (NoResultException ex) {
            throw new NoExceptionReportFoundException();
        }

        return exceptionReportEntity;
    }
    
        private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ExceptionReportEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
