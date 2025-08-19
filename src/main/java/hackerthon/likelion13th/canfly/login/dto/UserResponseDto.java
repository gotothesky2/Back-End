package hackerthon.likelion13th.canfly.login.dto;

import hackerthon.likelion13th.canfly.domain.entity.Sex;
import hackerthon.likelion13th.canfly.domain.user.Address;
import hackerthon.likelion13th.canfly.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private String name;
    private String email;
    private Sex sex;
    private String highschool;
    private Byte gradeNum;
    private int token;
    private Address address;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.sex = user.getSex();
        this.highschool = user.getHighschool();
        this.gradeNum = user.getGradeNum();
        this.token = user.getToken();
        this.address = user.getAddress();
    }
}
