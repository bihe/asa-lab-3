package at.ac.fhsalzburg.swd.spring.test.service.manualmock;

import java.util.List;
import java.util.Optional;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.ChairRepository;

public class MockChairRepository implements ChairRepository {

    @Override
    public Optional<Chair> findByName(String name) {
        switch (name) {
            case "existingChair":
                return Optional.of(new Chair("existingChair"));
            default:
                break;
        }
        return Optional.empty();
    }


    @Override
    @SuppressWarnings("unchecked")
    public <S extends Chair> S save(S entity) {
        if(entity == null)
            return null;
        return (S) new Chair("newChair");
    }

    @Override
    public <S extends Chair> Iterable<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Chair> findById(Long id) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterable<Chair> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Chair> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Chair entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends Chair> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }



    @Override
    public List<Chair> findByNameEndingWith(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}
