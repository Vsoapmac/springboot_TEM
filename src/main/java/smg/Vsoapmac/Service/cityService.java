package smg.Vsoapmac.Service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import smg.Vsoapmac.bean.city;

@Repository
@Service
public interface cityService extends JpaRepository<city, Integer> {
}
