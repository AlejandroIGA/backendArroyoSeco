package mx.edu.uteq.backend.dto;

import java.util.Date;

public class BookingResponseDTO {
    private Long id;
    private String status;
    private Date startDate;
    private Date endDate;
    private Long propertyId;
    private Long userId;
    //private PaymentDTO payment;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Long getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    // public PaymentDTO getPayment() {
    //     return payment;
    // }
    // public void setPayment(PaymentDTO payment) {
    //     this.payment = payment;
    // }
    
}
