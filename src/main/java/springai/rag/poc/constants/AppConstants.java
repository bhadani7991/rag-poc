package springai.rag.poc.constants;


import dev.langchain4j.internal.Utils;
import io.qdrant.client.grpc.Collections;

/**
 * Class to hold the constants.
 */
public class AppConstants {
    public static final String OPEN_API_KEY = "OPENAI_API_KEY";
    public static final String MODAL_NAME_KEY = "MODAL_NAME";
    public static final double TEMPERATURE = 0.3;
    public static final String COLLECTION_NAME = "langchain4j-" + Utils.randomUUID();
    public static final Collections.Distance DISTANCE = Collections.Distance.Cosine;
    public static final int DIMENSION = 384;
    public static final String QDRANT_HOST = "localhost";
    public static final String NEO4J_HOST = "neo4j+s://07bcf7eb.databases.neo4j.io";
    public static final String NEO4J_PASSWORD = "NEO4J_PASSWORD";
}
