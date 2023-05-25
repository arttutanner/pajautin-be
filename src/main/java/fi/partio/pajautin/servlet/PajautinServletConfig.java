package fi.partio.pajautin.servlet;




import com.fasterxml.jackson.core.util.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;


public class PajautinServletConfig extends ResourceConfig {
    public PajautinServletConfig() {

        property(ServerProperties.WADL_FEATURE_DISABLE, "true");

        // This registers all resources from the package & sub-packages
        packages("fi.partio.pajautin.rest");

        // bind things we want to run at servlet startup / reload / shutdown
        // here
        // register(ServletLifeCycleListener.class);

        // Session authentication filter & injector
        // register(SessionAuthenticationFilter.class);
        // register(SessionAuthorizationFilter.class);
        //System.out.println("Filter add");
        //register(ServletSecurityFilter.class);

        // Jersey internal, enable Jackson serialization
        register(JacksonFeature.class);

        // This registers the Jackson object mapper user for serialization /
        // de-serialization
        // register(ObjectMapperResolver.class);

        // register(PartialJsonObjectPatchReader.class);
        // register(JsonPatchReader.class);

        // if (Config.get(BooleanSetting.ACCESS_PRODUCTION_SERVERS))
        // SimpleLogger.disableDebug();
        //LoggerFactory.getLogger(this.getClass().toString()).info("Built ServletConfig");

        // BeanConfig beanConfig = new BeanConfig();
        // beanConfig.setVersion("0.0.1");
        // beanConfig.setSchemes(new String[]{"http"});
        // beanConfig.setHost("localhost:8080");
        // beanConfig.setBasePath("/domarest");
        // beanConfig.setResourcePackage("fi.invian.domarest.rest,
        // fi.invian.pojos.pojos, fi.invian.domarest.exceptions");
        // beanConfig.setScan(true);

        // Swagger
        // register(ApiListingResource.class);
        // register(SwaggerSerializers.class);

        // // enables out-of-the-box selecting fields with syntax like
        // /?select=a,b
        //
        // // registering this (hopefully) was the "cause" for a wild
        // [java.lang.ClassCastException:
        // sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl cannot
        // be cast to java.lang.Class] appearing randomly
        // // maybe relevant: https://java.net/jira/browse/JERSEY-2933

        // register(SelectableEntityFilteringFeature.class);
        register(CORSFilter.class);

    }
}
