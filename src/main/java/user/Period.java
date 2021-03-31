package user;

import java.sql.Date;

/**
 * représente une période
 */
public class Period
{
    private Date startDate;
    private Date endDate;

    public Period(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
