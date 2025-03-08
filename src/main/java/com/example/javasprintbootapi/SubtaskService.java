package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.SubtaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;



}
