import { ClipboardList, MessageCircle } from 'lucide-react';

import { FeatureCard } from './FeatureCard';

export const FeaturesSection = () => {
  const features = [
    {
      title: '실시간 커뮤니케이션',
      description:
        '실시간 채팅과 메일 시스템을 통한 원활한 소통. 팀원들과의 즉시적인 커뮤니케이션으로 업무 효율성을 극대화하세요.',
      features: ['실시간 채팅', '메일 시스템', '초대 및 관리'],
      icon: <MessageCircle className="w-10 h-10 text-[#467599] stroke-[2]" />,
      gradientFrom: '#467599',
      gradientTo: '#3a5f80',
      dotColor: '#467599',
    },
    {
      title: '프로젝트 관리',
      description:
        '마일스톤과 이슈 추적을 통한 체계적인 프로젝트 관리. 프로젝트의 모든 단계를 한눈에 파악하세요.',
      features: ['진행률 및 상태 확인', '마일스톤 추적', '이슈 관리'],
      icon: <ClipboardList className="w-10 h-10 text-[#A5C1D1] stroke-[2]" />,
      gradientFrom: '#A5C1D1',
      gradientTo: '#8fb0c2',
      dotColor: '#A5C1D1',
    },
  ];

  return (
    <section
      id="features"
      className="relative py-24 overflow-hidden"
      aria-labelledby="features-heading"
    >
      <div className="absolute inset-0 bg-gradient-to-br from-[#467599]/5 via-white to-[#A5C1D1]/5" />
      <div
        className="absolute inset-0 opacity-50"
        style={{
          backgroundImage:
            "url(\"data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23467599' fill-opacity='0.03'%3E%3Ccircle cx='30' cy='30' r='2'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E\")",
        }}
      />

      <div className="container mx-auto px-4 relative z-10">
        <header className="text-center mb-16">
          <div className="inline-flex items-center px-4 py-2 rounded-full bg-[#467599]/10 text-[#467599] text-sm font-medium mb-6">
            <span className="w-2 h-2 bg-[#467599] rounded-full mr-2" />
            핵심 기능
          </div>
          <h2 id="features-heading" className="text-4xl font-bold text-gray-900 mb-6">
            개발팀을 위한
            <span className="text-[#467599]"> 완벽한 협업 환경</span>
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto leading-relaxed break-keep">
            코드부터 커뮤니케이션까지, 개발팀의 모든 협업 요구사항을 하나의 플랫폼에서 해결합니다
          </p>
        </header>

        <div className="grid lg:grid-cols-2 gap-12 mb-16" role="list" aria-label="핵심 기능 목록">
          {features.map((feature, index) => (
            <FeatureCard key={index} {...feature} />
          ))}
        </div>
      </div>
    </section>
  );
};
