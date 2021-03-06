/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Koh Wen Jie
 */
@Entity
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationEntityId;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime reservationStartDate;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime reservationEndDate;
    @NotNull
    @Column(nullable = false)
    private Boolean isCheckedIn;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String lastName;
    @Email
    @NotNull
    @Column(nullable = false)
    private String email;
    @NotNull
    @Column(nullable = false, length = 8)
    @Size(min = 8, max = 8)
    private String contactNumber;
    @NotNull
    @Column(nullable = false, length = 8)
    @Size(min = 8, max = 8)
    private String passportNumber;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String roomTypeName;
    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal reservationPrice;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private RoomEntity roomEntity;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<RoomRateEntity> roomRateEntities;

    public ReservationEntity() {
        this.isCheckedIn = false;
        this.roomRateEntities = new ArrayList<RoomRateEntity>();
    }

    public ReservationEntity(LocalDateTime reservationStartDate, LocalDateTime reservationEndDate, String firstName, String lastName, String email, String contactNumber, String passportNumber, String roomTypeName, BigDecimal reservationPrice) {
        this();
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.passportNumber = passportNumber;
        this.roomTypeName = roomTypeName;
        this.reservationPrice = reservationPrice;
    }

    /**
     * @return the reservationEntityId
     */
    public Long getReservationEntityId() {
        return reservationEntityId;
    }

    /**
     * @param reservationEntityId the reservationEntityId to set
     */
    public void setReservationEntityId(Long reservationEntityId) {
        this.reservationEntityId = reservationEntityId;
    }

    /**
     * @return the reservationStartDate
     */
    public LocalDateTime getReservationStartDate() {
        return reservationStartDate;
    }

    /**
     * @param reservationStartDate the reservationDate to set
     */
    public void setReservationStartDate(LocalDateTime reservationStartDate) {
        this.reservationStartDate = reservationStartDate;
    }

    /**
     * @return the reservationEndDate
     */
    public LocalDateTime getReservationEndDate() {
        return reservationEndDate;
    }

    /**
     * @param reservationEndDate the reservationEndDate to set
     */
    public void setReservationEndDate(LocalDateTime reservationEndDate) {
        this.reservationEndDate = reservationEndDate;
    }

    /**
     * @return the checkedIn
     */
    public Boolean getIsCheckedIn() {
        return isCheckedIn;
    }

    /**
     * @param checkedIn the checkedIn to set
     */
    public void setIsCheckedIn(Boolean isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the contactNumber
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * @param contactNumber the contactNumber to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * @return the passportNumber
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * @param passportNumber the passportNumber to set
     */
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    /**
     * @return the roomTypeName
     */
    public String getRoomTypeName() {
        return roomTypeName;
    }

    /**
     * @param roomTypeName the roomTypeName to set
     */
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    /**
     * @return the roomEntity
     */
    public RoomEntity getRoomEntity() {
        return roomEntity;
    }

    /**
     * @param roomEntity the roomEntity to set
     */
    public void setRoomEntity(RoomEntity roomEntity) {
        this.roomEntity = roomEntity;
    }

    /**
     * @return the reservationPrice
     */
    public BigDecimal getReservationPrice() {
        return reservationPrice;
    }

    /**
     * @param reservationPrice the reservationPrice to set
     */
    public void setReservationPrice(BigDecimal reservationPrice) {
        this.reservationPrice = reservationPrice;
    }

    /**
     * @return the roomRateEntity
     */
    public List<RoomRateEntity> getRoomRateEntities() {
        return roomRateEntities;
    }

    /**
     * @param roomRateEntity the roomRateEntity to set
     */
    public void setRoomRateEntities(List<RoomRateEntity> roomRateEntities) {
        this.roomRateEntities = roomRateEntities;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationEntityId != null ? reservationEntityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the resversationEntityId fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationEntityId == null && other.reservationEntityId != null) || (this.reservationEntityId != null && !this.reservationEntityId.equals(other.reservationEntityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationEntity[ id=" + reservationEntityId + " ]";
    }

}
