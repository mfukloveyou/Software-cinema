package Manager;

import model.Ticket;

import java.util.List;

public interface TicketManager {
    boolean bookTicket(Ticket ticket);  // Đặt vé
    List<Ticket> getTicketsByUserId(int userId);  // Lấy danh sách vé của người dùng
    boolean cancelTicket(int ticketId);  // Hủy vé
}
