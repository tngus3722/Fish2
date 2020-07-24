package Service;

import Domain.User;
import Repository.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public boolean isValidToken(String jwt){
        Claims claims = Jwts.parser().setSigningKey("a".getBytes()).parseClaimsJws(jwt).getBody();
        Date now = new Date();
        String name = claims.get("name", String.class); // jwt payload -> name
        String password = claims.get("password", String.class); // jwt payload -> password
        Date exp = claims.get("exp", Date.class); // jwt payload -> exp

        User user = userMapper.getUserByName(name);
        System.out.println(user.getName() + " " + name);
        System.out.println(user.getPassword() + " " + password);
        System.out.println(now + " " + exp);
        System.out.println(now.getTime()+ " " + exp.getTime() + " " + (now.getTime() - exp.getTime()));

        if( exp.getTime() > now.getTime() && user.getName().equals(name) && user.getPassword().equals(password) ){ // 만료시간 유효 & jwt의 내용이 database user 내용과 일치한다면
            return true;
        }
        else
            return false;
    }

    public String CreateJwt(String name, String password){ // 토큰 생성
        Map<String, Object> headers = new HashMap<String, Object>(); // header
        headers.put("typ", "JWT");
        headers.put("alg","HS256");
        Map<String, Object> payloads = new HashMap<String, Object>(); //payload
        payloads.put("name",name); // name
        payloads.put("password",password); // password
        Long tmp = 11l*1000l * 60l; // 10분
        Date exp = new Date();
        exp.setTime( (exp.getTime() + tmp) ); // 현재시간  + 11분

        String key = "a"; // secret key
        String jwt = Jwts.builder().setHeader(headers).setClaims(payloads).setExpiration(exp).signWith(SignatureAlgorithm.HS256, key.getBytes()).compact();
        return jwt;
    }

    public void signUp(User user){ // 회원가입
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); //bcrypt 암호화
        userMapper.signUp(user);
    }

    public boolean logIn(User user, HttpServletResponse response){
        User tmp = userMapper.getUserByName(user.getName()); // 해당 id에 암호화 된 password 조회

        if ( tmp == null ) // 없는 id 라면 false
            return false;

        else if ( BCrypt.checkpw(user.getPassword(), tmp.getPassword() ) ) {// 입력 plain password, database에 암호화된 암호 비교
            String jwt = CreateJwt(user.getName(), tmp.getPassword());
            Cookie cookie = new Cookie("token", jwt); // 쿠기가 없다면
            cookie.setMaxAge(10*60); // 5분
            cookie.setHttpOnly(true); // XSS 공격을 막음
            response.addCookie(cookie);
            return true;
        }
        else
            return false;
    }

    public void logOut(HttpServletResponse response){
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}