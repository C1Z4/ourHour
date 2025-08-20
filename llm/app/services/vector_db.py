import chromadb
from langchain.embeddings import OpenAIEmbeddings
from langchain.vectorstores import Chroma
from langchain.document_loaders import TextLoader
from langchain.text_splitter import CharacterTextSplitter

class VectorDBService:
    def __init__(self):
        self.embeddings = OpenAIEmbeddings()
        self.client = chromadb.Client()
        self.collection_name = "company_docs"
        
    def add_documents(self, file_path: str):
        # 문서 로드 및 분할
        loader = TextLoader(file_path, encoding='utf-8')
        documents = loader.load()
        
        text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=0)
        docs = text_splitter.split_documents(documents)
        
        # Vector DB에 저장
        vectordb = Chroma.from_documents(
            documents=docs,
            embedding=self.embeddings,
            collection_name=self.collection_name
        )
        
        return vectordb
    
    def similarity_search(self, query: str, k: int = 3):
        vectordb = Chroma(
            embedding_function=self.embeddings,
            collection_name=self.collection_name
        )
        return vectordb.similarity_search(query, k=k)

vector_service = VectorDBService()
