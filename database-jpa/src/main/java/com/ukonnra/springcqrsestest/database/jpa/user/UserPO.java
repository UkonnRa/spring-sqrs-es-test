package com.ukonnra.springcqrsestest.database.jpa.user;

import com.ukonnra.springcqrsestest.database.jpa.AbstractEntityPO;
import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import com.ukonnra.springcqrsestest.shared.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Entity(name = User.TYPE)
@Table(name = User.TYPE)
public class UserPO extends AbstractEntityPO<User> {
  @Size(min = AbstractEntity.MIN_NAMELY, max = AbstractEntity.MAX_NAMELY)
  @Column(nullable = false)
  private String loginName;

  @Size(min = AbstractEntity.MIN_NAMELY, max = AbstractEntity.MAX_NAMELY)
  @Column(nullable = false)
  private String displayName;

  public UserPO(final User user) {
    super(user);
    this.loginName = user.getLoginName();
    this.displayName = user.getDisplayName();
  }

  @Override
  public void load(User entity) {
    super.load(entity);
    entity.setLoginName(this.loginName);
    entity.setDisplayName(this.displayName);
  }
}
