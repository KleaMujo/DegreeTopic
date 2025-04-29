//package al.easybill.app.easybillproject.Service;
//
//import al.easybill.app.easybillproject.Model.User;
//import al.easybill.app.easybillproject.Repository.UserRepository;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Here findByUsername returns an Optional<User>
//        User user = userRepository.findByUsername(username);
//
//
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        // If user exists, map it to UserDetails
//        return (UserDetails) mapToUserDetails(user);
//    }
//
//    private UserDetails mapToUserDetails(User user) {
//        return (UserDetails) new User(
//                user.getUsername(),
//                user.getPassword()
//                );
//
//    }
//}
