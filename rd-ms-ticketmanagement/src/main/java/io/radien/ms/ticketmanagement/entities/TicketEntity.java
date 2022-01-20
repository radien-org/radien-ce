package io.radien.ms.ticketmanagement.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TIC01")
public class TicketEntity extends io.radien.ms.ticketmanagement.client.entities.Ticket{

    private static final long serialVersionUID = -8383376728635914496L;

    public TicketEntity(){}

    public TicketEntity(io.radien.ms.ticketmanagement.client.entities.Ticket ticket){super(ticket);}

    @Id
    @TableGenerator(name = "GEN_SEQ_TIC01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TIC01")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column
    @Override
    public Long getUserId() {
        return super.getUserId();
    }

    @Column
    @Override
    public Long getTicketType() {
        return super.getTicketType();
    }

    @Column
    @Override
    public LocalDate getExpireDate() {
        return super.getExpireDate();
    }

    @Column (unique = true)
    @Override
    public String getToken() {
        return super.getToken();
    }

    @Column
    @Override
    public String getData() {
        return super.getData();
    }

}
