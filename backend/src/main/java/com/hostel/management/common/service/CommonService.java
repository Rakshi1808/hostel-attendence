package com.hostel.management.common.service;

import com.hostel.management.admin.Admin;
import com.hostel.management.admin.AdminRepository;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.exception.StudentNotFoundException;
import com.hostel.management.student.repository.StudentRepository;
import com.hostel.management.warden.Warden;
import com.hostel.management.warden.WardenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonService
{
    private final StudentRepository studentRepository;
    private final WardenRepository wardenRepository;
    private final AdminRepository adminRepository;
    public CommonService(StudentRepository studentRepository, WardenRepository wardenRepository, AdminRepository adminRepository)
    {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.wardenRepository = wardenRepository;
    }
    public Student getCurrentStudent()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return studentRepository.findById(username)
                .orElseThrow(()-> new StudentNotFoundException("Student Not Found"));

    }
    public Warden getCurrentWarden()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer username = Integer.valueOf(authentication.getName());
        return wardenRepository.findById(username)
                .orElseThrow(() -> new StudentNotFoundException("Warden Not Found"));
    }

    public Admin getCurrentAdmin()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer username = Integer.valueOf(authentication.getName());
        return adminRepository.findById(username)
                .orElseThrow(() -> new StudentNotFoundException("Warden Not Found"));
    }


}
