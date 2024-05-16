package com.mysite.sbb.repository;

import com.mysite.sbb.entity.R_Container;
import com.mysite.sbb.entity.ShinyApp;
import com.mysite.sbb.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface R_ContainerRepository extends JpaRepository<R_Container,Integer> {
    R_Container findByAuthor(SiteUser author);

    @Query("SELECT r.portNumber FROM R_Container r")
    List<String> findAllPortNumbers();

}
