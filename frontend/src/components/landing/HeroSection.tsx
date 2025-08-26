import { Link } from '@tanstack/react-router';

import { MonitorMockup } from '@/components/landing/MonitorMockup';
import { Button } from '@/components/ui/button';

export function HeroSection() {
  return (
    <section className="container mx-auto px-4 py-16" aria-labelledby="hero-heading" role="banner">
      <div className="grid lg:grid-cols-3 gap-12 items-center">
        <article className="lg:col-span-2 space-y-8" aria-labelledby="hero-heading">
          <header className="space-y-4">
            <h1
              id="hero-heading"
              className="text-4xl lg:text-6xl font-bold text-gray-900 leading-tight"
            >
              개발자를 위한, 개발자에 의한
              <br />
              <span className="text-[#467599]">그룹웨어</span>
            </h1>
          </header>

          <h2 className="text-xl lg:text-2xl font-semibold text-gray-700 leading-relaxed">
            코드 협업부터 일정 관리까지, 하나로 끝낸다
          </h2>

          <p className="text-lg text-gray-600 leading-relaxed max-w-2xl">
            개발팀의 생산성을 극대화하는 올인원 협업툴
          </p>

          <div className="flex flex-col sm:flex-row gap-4 pt-4">
            <Button
              asChild
              size="lg"
              className="bg-[#467599] hover:bg-[#3a5f80] text-white px-8 py-3 text-lg font-semibold shadow-lg hover:shadow-xl transition-all duration-200"
              aria-label="서비스 시작하기"
            >
              <Link to="/start" search={{ page: 1 }}>
                지금 시작하기
              </Link>
            </Button>
          </div>
        </article>

        <aside className="lg:col-span-1 relative" aria-label="서비스 미리보기">
          <div className="relative">
            <div className="absolute inset-0 bg-gradient-to-br from-[#467599]/20 to-[#A5C1D1]/20 rounded-2xl blur-xl" />

            <div className="relative bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-2xl border border-white/20">
              <div className="space-y-4">
                <div className="flex gap-3">
                  <MonitorMockup />
                  <MonitorMockup />
                </div>

                <div className="flex gap-3">
                  <MonitorMockup />
                  <MonitorMockup />
                </div>
              </div>

              <div className="mt-4 bg-gray-200 rounded-lg h-4 relative">
                <div className="absolute -bottom-2 left-1/2 transform -translate-x-1/2 w-16 h-2 bg-gray-300 rounded" />
              </div>
            </div>
          </div>
        </aside>
      </div>
    </section>
  );
}
