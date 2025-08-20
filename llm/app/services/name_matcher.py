"""
이름 매칭 유틸리티
다양한 이름 입력에 대해 정확한 멤버를 찾기 위한 유틸리티
"""

from typing import List, Dict, Tuple, Optional
import re
from difflib import get_close_matches


class NameMatcher:
    """이름 매칭을 위한 유틸리티 클래스"""
    
    def __init__(self, member_names: List[str]):
        """
        Args:
            member_names: 조직 내 모든 멤버 이름 리스트
        """
        self.member_names = member_names
        self.name_variations = self._build_name_variations()
    
    def _build_name_variations(self) -> Dict[str, str]:
        """이름 변형 사전 생성"""
        variations = {}
        
        for name in self.member_names:
            # 원본 이름
            variations[name.lower()] = name
            variations[name.replace(' ', '').lower()] = name
            
            # 성만 추출 (한국식 이름)
            if len(name) >= 2:
                lastname = name[0]
                if lastname not in variations:
                    variations[lastname] = name
                
            # 이름만 추출 (한국식 이름)
            if len(name) >= 3:
                firstname = name[1:]
                variations[firstname.lower()] = name
        
        return variations
    
    def find_best_match(self, query_name: str, threshold: float = 0.6) -> Optional[Tuple[str, float]]:
        """
        가장 유사한 이름 찾기
        
        Args:
            query_name: 검색할 이름
            threshold: 유사도 임계값 (0.0 ~ 1.0)
            
        Returns:
            (매칭된 이름, 유사도) 튜플 또는 None
        """
        query_clean = query_name.replace(' ', '').lower()
        
        # 1. 정확한 매칭 시도
        if query_clean in self.name_variations:
            return (self.name_variations[query_clean], 1.0)
        
        # 2. 부분 매칭 시도
        for variation, original_name in self.name_variations.items():
            if query_clean in variation or variation in query_clean:
                similarity = min(len(query_clean), len(variation)) / max(len(query_clean), len(variation))
                if similarity >= threshold:
                    return (original_name, similarity)
        
        # 3. 유사도 매칭 (difflib 사용)
        close_matches = get_close_matches(
            query_clean, 
            self.name_variations.keys(), 
            n=1, 
            cutoff=threshold
        )
        
        if close_matches:
            matched_variation = close_matches[0]
            original_name = self.name_variations[matched_variation]
            
            # 유사도 계산
            similarity = self._calculate_similarity(query_clean, matched_variation)
            return (original_name, similarity)
        
        return None
    
    def find_multiple_matches(self, query_name: str, max_results: int = 5, threshold: float = 0.4) -> List[Tuple[str, float]]:
        """
        여러 개의 유사한 이름 찾기
        
        Args:
            query_name: 검색할 이름
            max_results: 최대 결과 개수
            threshold: 유사도 임계값
            
        Returns:
            [(이름, 유사도), ...] 리스트 (유사도 순으로 정렬)
        """
        query_clean = query_name.replace(' ', '').lower()
        matches = []
        
        # 모든 이름에 대해 유사도 계산
        for original_name in self.member_names:
            name_clean = original_name.replace(' ', '').lower()
            similarity = self._calculate_similarity(query_clean, name_clean)
            
            if similarity >= threshold:
                matches.append((original_name, similarity))
        
        # 유사도 순으로 정렬하고 상위 결과만 반환
        matches.sort(key=lambda x: x[1], reverse=True)
        return matches[:max_results]
    
    def _calculate_similarity(self, str1: str, str2: str) -> float:
        """두 문자열 간 유사도 계산"""
        if str1 == str2:
            return 1.0
        
        # Levenshtein distance 기반 유사도
        from difflib import SequenceMatcher
        matcher = SequenceMatcher(None, str1, str2)
        return matcher.ratio()
    
    def suggest_names(self, query_name: str, max_suggestions: int = 3) -> List[str]:
        """
        이름 제안 기능
        
        Args:
            query_name: 검색할 이름
            max_suggestions: 최대 제안 개수
            
        Returns:
            제안할 이름 리스트
        """
        matches = self.find_multiple_matches(query_name, max_suggestions, threshold=0.3)
        return [name for name, _ in matches]


def extract_person_name_from_question(question: str) -> Optional[str]:
    """
    질문에서 사람 이름 추출
    
    Args:
        question: 사용자 질문
        
    Returns:
        추출된 이름 또는 None
    """
    # 한국어 이름 패턴 (김xx, 이xx 등)
    korean_name_patterns = [
        r'([김이박최정강조윤장임한오서신권황안송전홍유고문양손배조백허성민노하정차신서]'
        r'[가-힣]{1,3})(?:의|은|는|이|가|씨|님|사원|대리|과장|부장|팀장|이사|상무|전무|사장)?',
        
        # "김아워라는 사람" 패턴
        r'([가-힣]{2,4})(?:라는 사람|라는|이라는)',
        
        # "김아워 직책" 패턴  
        r'([가-힣]{2,4})(?:의|은|는|이|가)?\s*(?:직책|직급|부서|연락처|전화번호|이메일)',
        
        # 일반적인 한국 이름 패턴
        r'([가-힣]{2,4})(?:[^\w가-힣]|$)'
    ]
    
    for pattern in korean_name_patterns:
        matches = re.findall(pattern, question)
        if matches:
            # 가장 긴 매치를 반환 (더 완전한 이름일 가능성)
            return max(matches, key=len)
    
    return None


# 테스트 함수
def test_name_matcher():
    """이름 매처 테스트"""
    test_members = ["김아워", "이시간", "박분단", "최예약", "정회의", "강프로젝트"]
    matcher = NameMatcher(test_members)
    
    test_queries = [
        "김아워",      # 정확한 매칭
        "김아",        # 부분 매칭
        "아워",        # 이름 부분
        "김아우",      # 유사한 이름
        "이시",        # 성 + 이름 첫 글자
        "시간",        # 이름 부분
    ]
    
    print("=== 이름 매칭 테스트 ===")
    for query in test_queries:
        result = matcher.find_best_match(query)
        if result:
            name, similarity = result
            print(f"'{query}' -> '{name}' (유사도: {similarity:.2f})")
        else:
            print(f"'{query}' -> 매칭 없음")
        
        # 다중 매칭 결과
        multiple = matcher.find_multiple_matches(query, max_results=3)
        if multiple:
            print(f"  다중 매칭: {multiple}")
        print()


if __name__ == "__main__":
    test_name_matcher()