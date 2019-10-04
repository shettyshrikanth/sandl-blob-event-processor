package uk.gov.moj.cpp.sandl.persistence.repository;

import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;

import java.util.List;
import java.util.logging.Level;

import com.microsoft.azure.functions.ExecutionContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CourtScheduleRepository {
    private Configuration configuration = new HibernateConfiguration().createHibernateConfiguration();

    public boolean save(final List<CourtSchedule> records, final ExecutionContext context) {

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            context.getLogger().info("Started saving court schedules\n");

            session.beginTransaction();

            for (final CourtSchedule courtSchedule : records) {
                session.save(courtSchedule);
            }

            session.getTransaction().commit();

            context.getLogger().info("Saved all court schedules successfully \n");
        } catch (Exception e) {
            context.getLogger().log(Level.SEVERE, "Exception during save to DB ", e);
            return false;
        }

        return true;
    }
}
