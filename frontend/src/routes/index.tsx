import { useEffect } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { FeaturesSection } from '@/components/landing/FeaturesSection';
import { Footer } from '@/components/landing/Footer';
import { Header } from '@/components/landing/Header';
import { HeroSection } from '@/components/landing/HeroSection';
import { restoreAuthFromServer } from '@/utils/auth/tokenUtils';

export const Route = createFileRoute('/')({
  component: LandingPage,
});

function LandingPage() {
  useEffect(() => {
    restoreAuthFromServer();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-teal-50">
      <Header />
      <HeroSection />
      <FeaturesSection />
      <Footer />
    </div>
  );
}
