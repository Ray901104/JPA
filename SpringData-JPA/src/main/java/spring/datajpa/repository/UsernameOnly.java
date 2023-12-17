package spring.datajpa.repository;

public interface UsernameOnly {
//    @Value("#{target.username + ' ' + target.age}") //Opened Projection
    String getUsername();
}
