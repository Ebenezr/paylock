package com.blind.paylock.component;

import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.model.User;


public interface UserComponent {

    User buildNewUser(UserCreateRequestDto request);
}
