package jersey.java8;

import com.google.cloud.Timestamp;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class UserEntity {
  @Id public String id;
  public String name;
  public Timestamp createdAt;
}
