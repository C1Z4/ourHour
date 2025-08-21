import os
from dotenv import load_dotenv
from langchain_openai import OpenAIEmbeddings
from langchain_pinecone import PineconeVectorStore
from langchain.docstore.document import Document
from pinecone import Pinecone

if os.path.exists('/etc/secrets/env'):
    load_dotenv('/etc/secrets/env')

embedding_model = OpenAIEmbeddings(model="text-embedding-3-small")

class PineconeService:
    def __init__(self):

        self.index_name = os.getenv("PINECONE_INDEX_NAME")

        if not self.index_name:
            raise ValueError("PINECONE_INDEX_NAME 환경 변수를 설정해주세요.")

        # pinecone index 연결
        try:
            self.vector_store = PineconeVectorStore.from_existing_index(
                index_name=self.index_name,
                embedding=embedding_model
            )
            print(f"'{self.index_name}' 인덱스에 성공적으로 연결되었습니다.")
        except Exception as e:
            print(f"'{self.index_name}' 인덱스를 찾을 수 없거나 연결에 실패했습니다: {e}")
            raise

    # LangChain의 Document 객체 리스트를 받아서 Pinecone에 저장
    def upsert_documents(self, documents: list[Document]):
        self.vector_store.add_documents(documents)
        print(f"{len(documents)}개의 문서를 성공적으로 저장했습니다.")

    # 사용자의 질문(query)과 가장 유사한 문서를 k개 검색해서 반환
    def search_documents(self, query: str, k: int = 3) -> list[Document]:
        similar_docs = self.vector_store.similarity_search(query, k=k)
        return similar_docs

# --- 앱 전체에서 사용할 서비스 객체 생성 ---
pinecone_service = PineconeService()