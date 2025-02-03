package springai.rag.poc.constants;


/**
 * Class to hold the constants.
 */
public class AppConstants {
    public static final String OPEN_API_KEY = "OPENAI_API_KEY";
    public static final String QDRANT_API_KEY = "QDRANT_API_KEY";
    public static final int MIN_THREAD_POOL_SIZE = 10;
    public static final String METADATA_KEY = "topic";
    public static final String INGESTION_SUCCESS_RESPONSE = "Successfully stored the embeddings into Embedding Store";
    public static final String EMBEDDING_STORE_CLEAR_RESPONSE = "Successfully removed all the content from Embedding Store";
    public static final String PINECONE_API_KEY = "PINECONE_API_KEY";

    public static class ContentType{
        public static final String APPLICATION_PDF = "application/pdf";
        public static final String APPLICATION_TEXT="text/plain";
    }
}
