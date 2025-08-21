import os
from dotenv import load_dotenv
from google.cloud import secretmanager


def load_secret_env():
    """
    Google Cloud Secret Manager에서 환경변수를 로드하는 유틸리티 함수
    
    Secret Manager에서 ourhour-llm 시크릿을 가져와서 환경변수로 설정합니다.
    실패시 로컬 파일 fallback을 시도합니다.
    """
    try:
        # Secret Manager 클라이언트 생성
        client = secretmanager.SecretManagerServiceClient()
        
        # 시크릿 경로 설정
        secret_name = "projects/1022751668871/secrets/ourhour-llm/versions/latest"
        
        # 시크릿 값 가져오기
        response = client.access_secret_version(request={"name": secret_name})
        secret_data = response.payload.data.decode("UTF-8")
        
        # .env 형식의 데이터를 파싱하여 환경변수로 설정
        for line in secret_data.strip().split('\n'):
            if line and '=' in line:
                key, value = line.split('=', 1)
                os.environ[key.strip()] = value.strip()
                
        print("Secret Manager에서 환경변수 로드 완료")
        return True
        
    except Exception as e:
        print(f"Secret Manager 로드 실패: {e}")
        
        # 로컬 개발 환경을 위한 fallback
        if os.path.exists('/etc/secrets/env'):
            print("로컬 시크릿 파일에서 로드 시도")
            load_dotenv('/etc/secrets/env')
            return True
        elif os.path.exists('.env'):
            print("로컬 .env 파일에서 로드 시도")
            load_dotenv('.env')
            return True
        else:
            print("환경변수 로드 실패 - 모든 fallback 실패")
            return False


def get_required_env(key: str, default: str = None) -> str:
    """
    필수 환경변수를 가져오는 헬퍼 함수
    
    Args:
        key: 환경변수 키
        default: 기본값 (None인 경우 키가 없으면 에러)
    
    Returns:
        환경변수 값
        
    Raises:
        ValueError: 필수 환경변수가 설정되지 않은 경우
    """
    value = os.getenv(key, default)
    if value is None:
        raise ValueError(f"필수 환경변수 '{key}'가 설정되지 않았습니다.")
    return value