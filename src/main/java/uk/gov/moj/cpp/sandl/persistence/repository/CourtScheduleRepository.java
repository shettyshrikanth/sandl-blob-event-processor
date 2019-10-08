package uk.gov.moj.cpp.sandl.persistence.repository;

import static java.lang.String.format;
import static uk.gov.moj.cpp.sandl.persistence.repository.C3poDataSource.getConnection;

import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;

import com.microsoft.azure.functions.ExecutionContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CourtScheduleRepository {
    private static final String INSERT_QRY = "INSERT INTO CourtSchedule (id, oucode, startDate, enddate,  max_slots, canOverList, iswelsh) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private Configuration configuration = new HibernateConfiguration().createHibernateConfiguration();

    public boolean save(final List<CourtSchedule> records, final ExecutionContext context) {

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            context.getLogger().info("Started saving court schedules\n");

            session.beginTransaction();
            int count =1;

            for (final CourtSchedule courtSchedule : records) {
                session.save(courtSchedule);
                context.getLogger().info(format("Saved %d schedule", count++));
            }

            session.getTransaction().commit();

            context.getLogger().info("Saved all court schedules successfully \n");
        } catch (Exception e) {
            context.getLogger().log(Level.SEVERE, "Exception during save to DB ", e);
            return false;
        }

        return true;
    }
    public boolean saveJdbc(final List<CourtSchedule> records, final ExecutionContext context) {
        final Connection connection = getConnection();
        try  {
            context.getLogger().info("Started saving court schedules\n");

            final PreparedStatement query = connection.prepareStatement(INSERT_QRY);

            for (final CourtSchedule courtSchedule : records) {
                query.setString(1, courtSchedule.getId());
                query.setString(2, courtSchedule.getOucode());
                query.setObject(3, courtSchedule.getStartDate());
                //   query.setObject(4, courtSchedule.getStartTime());
                query.setObject(4, courtSchedule.getEndDate());
                //   query.setObject(6, courtSchedule.getEndTime());
                query.setInt(5, courtSchedule.getMaxSlots());
                query.setBoolean(6, courtSchedule.getCanOverList());
                query.setBoolean(7, courtSchedule.getWelsh());
                query.executeUpdate();
                query.clearParameters();
            }

            connection.commit();
            context.getLogger().info("Saved all court schedules successfully \n");
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                context.getLogger().log(Level.SEVERE, "Exception during during rollback to DB ", e);
            }
            context.getLogger().log(Level.SEVERE, "Exception during save to DB ", e);
            return false;
        }

        return true;
    }
}
