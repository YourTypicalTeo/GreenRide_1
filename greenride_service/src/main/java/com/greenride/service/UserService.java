package com.greenride.service;

import com.greenride.dto.RegisterRequest;
import com.greenride.model.User;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
}