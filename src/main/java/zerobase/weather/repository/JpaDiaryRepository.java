package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;
import java.util.zip.DataFormatException;

@Repository
public interface JpaDiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate start, LocalDate end);

    Diary getFirstByDate(LocalDate date);

    void deleteAllByDate(LocalDate date);


}
