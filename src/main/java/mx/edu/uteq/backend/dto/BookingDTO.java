package mx.edu.uteq.backend.dto;

import java.util.Date;

public class BookingDTO {
    private Long id;
    private Long propertyId;
    private Long userId;
    private String status;
    private Date startDate;
    private Date endDate;

    public BookingDTO() {}

    public BookingDTO(Long id, Long propertyId, Long userId, String status, Date startDate, Date endDate) {
        this.id = id;
        this.propertyId = propertyId;
        this.userId = userId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}