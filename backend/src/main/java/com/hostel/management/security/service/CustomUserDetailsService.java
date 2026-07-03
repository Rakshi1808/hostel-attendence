package com.hostel.management.security.service;

import com.hostel.management.admin.Admin;
import com.hostel.management.admin.AdminRepository;
import com.hostel.management.auth.exception.InvalidCredentialsException;
import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.enums.Role;
import com.hostel.management.security.user.CustomUserDetails;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.repository.StudentRepository;
import com.hostel.management.warden.Warden;
import com.hostel.management.warden.WardenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final StudentRepository studentRepository;
    private final WardenRepository wardenRepository;
    private final AdminRepository adminRepository;
    public CustomUserDetailsService(StudentRepository studentRepository,
                                    WardenRepository wardenRepository, AdminRepository adminRepository)
    {
    this.studentRepository = studentRepository;
    this.wardenRepository = wardenRepository;
    this.adminRepository = adminRepository;
    }

    private UserDetails createUserDetails(String username,String password,Role role,boolean enabled)
    {
        return CustomUserDetails.builder()
                .username(username)
                .password(password)
                .role(role)
                .enabled(enabled)
                .build();
    }

    public UserDetails loadUserByUsername(String username)
    {
        Optional<Student> student1 = studentRepository.findById(username);
            if(student1.isPresent())
            {
            Student student = student1.get();

            if(student.getAccountStatus()!= AccountStatus.SUSPENDED)
            {
                return createUserDetails(username,student.getPassword(),Role.STUDENT,true);
            }
            else
            {
                throw new InvalidCredentialsException("Account status is SUSPENDED");
            }
        }

        Optional<Warden> warden = wardenRepository.findByEmail(username);
        if(warden.isPresent())
        {
            Warden warden1 = warden.get();
            if(warden1.getIsActive())
            {
                return createUserDetails(username,warden1.getPassword(),Role.WARDEN,true);
            }
            else
            {
                throw new InvalidCredentialsException("username is not active");
            }
        }


        Optional<Admin> admin = adminRepository.findByEmail(username);
        if(admin.isPresent())
        {

            Admin admin1 = admin.get();
            if(admin1.getIsActive())
            {
                return createUserDetails(username,admin1.getPassword(),Role.ADMIN,true);
            }
            else
            {
                throw new InvalidCredentialsException("username is not active");
            }

        }
        throw new UsernameNotFoundException("Invalid username");
    }
}
