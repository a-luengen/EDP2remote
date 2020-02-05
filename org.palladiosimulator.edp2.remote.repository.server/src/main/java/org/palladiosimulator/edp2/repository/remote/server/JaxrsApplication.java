package org.palladiosimulator.edp2.repository.remote.server;

import org.springframework.stereotype.Component;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@Component
@ApplicationPath("/edp2/")
public class JaxrsApplication extends Application {
	
}