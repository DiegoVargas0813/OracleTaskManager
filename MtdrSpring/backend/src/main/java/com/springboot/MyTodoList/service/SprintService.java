package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SprintService {
    @Autowired
    private SprintRepository sprintRepository;

    public Sprint createSprint(Sprint sprint){
        return sprintRepository.save(sprint);
    }

    public List<Sprint> getAllSprints(){
        return sprintRepository.findAll();
    }

    public Optional<Sprint> getSprintById(int id){
        return sprintRepository.findById(id);
    }

    public List<Sprint> getActiveSprints() {
        OffsetDateTime now = OffsetDateTime.now();
        return sprintRepository.findByStartDateBeforeAndEndDateAfter(now, now);
    }

    public List<Sprint> getSprintsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return sprintRepository.findByStartDateBeforeAndEndDateAfter(startDate, endDate);
    }
}
