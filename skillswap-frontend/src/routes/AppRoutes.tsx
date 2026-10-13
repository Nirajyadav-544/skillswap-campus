import type { ReactNode } from "react";

import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import LoadingScreen from "../components/common/LoadingScreen";
import { useAuth } from "../hooks/useAuth";

import HomePage from "../pages/HomePage";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import VerifyOtpPage from "../pages/auth/VerifyOtpPage";
import ForgotPasswordPage from "../pages/auth/ForgotPasswordPage";
import DashboardPage from "../pages/dashboard/DashboardPage";

import ProfilePage from "../pages/profile/ProfilePage";
import EditProfilePage from "../pages/profile/EditProfilePage";

import SkillsPage from "../pages/skills/SkillsPage";
import MySkillsPage from "../pages/skills/MySkillsPage";
import AddSkillPage from "../pages/skills/AddSkillPage";
import EditSkillPage from "../pages/skills/EditSkillPage";

import SkillDNAPage from "../pages/skill/SkillDNAPage";

import MatchingPage from "../pages/matching/MatchingPage";
import ExchangePage from "../pages/exchange/ExchangePage";
import ChatPage from "../pages/chat/ChatPage.tsx";

import StudentDiscoveryPage from "../pages/students/StudentDiscoveryPage";
import StudentProfilePage from "../pages/students/StudentProfilePage";
import MessagePage from "../pages/students/MessagePage";
import VideoCallPage from "../pages/students/VideoCallPage";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({
  children,
}: ProtectedRouteProps) => {
  const {
    isAuthenticated,
    isLoading,
  } = useAuth();

  if (isLoading) {
    return (
      <LoadingScreen
        message="Restoring your SkillSwap Campus session..."
      />
    );
  }

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return <>{children}</>;
};

interface PublicRouteProps {
  children: ReactNode;
}

const PublicRoute = ({
  children,
}: PublicRouteProps) => {
  const {
    isAuthenticated,
    isLoading,
  } = useAuth();

  if (isLoading) {
    return (
      <LoadingScreen
        message="Loading SkillSwap Campus..."
      />
    );
  }

  if (isAuthenticated) {
    return (
      <Navigate
        to="/dashboard"
        replace
      />
    );
  }

  return <>{children}</>;
};

interface ModulePlaceholderProps {
  title: string;
  description: string;
}

const ModulePlaceholder = ({
  title,
  description,
}: ModulePlaceholderProps) => {
  return (
    <main className="min-h-[calc(100vh-136px)] bg-slate-50 px-4 py-10 sm:px-6 lg:px-8">
      <div className="mx-auto w-full max-w-6xl">
        <section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm sm:p-10">
          <div className="mb-5 grid h-14 w-14 place-items-center rounded-2xl bg-indigo-50 text-lg font-extrabold text-indigo-600">
            SS
          </div>

          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            {title}
          </h1>

          <p className="mt-3 max-w-2xl text-sm leading-7 text-slate-600 sm:text-base">
            {description}
          </p>
        </section>
      </div>
    </main>
  );
};

const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>

        {/* Home */}

        <Route
          path="/"
          element={<HomePage />}
        />

        {/* Authentication */}

        <Route
          path="/login"
          element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          }
        />

        <Route
          path="/register"
          element={
            <PublicRoute>
              <RegisterPage />
            </PublicRoute>
          }
        />

        <Route
          path="/forgot-password"
          element={
            <PublicRoute>
              <ForgotPasswordPage />
            </PublicRoute>
          }
        />

        <Route
          path="/verify-otp"
          element={<VerifyOtpPage />}
        />

        {/* Dashboard */}

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        {/* Profile */}

        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/profile/edit"
          element={
            <ProtectedRoute>
              <EditProfilePage />
            </ProtectedRoute>
          }
        />

        {/* Skills */}

        <Route
          path="/skills"
          element={
            <ProtectedRoute>
              <SkillsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/skills/my"
          element={
            <ProtectedRoute>
              <MySkillsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/skills/add"
          element={
            <ProtectedRoute>
              <AddSkillPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/skills/edit/:id"
          element={
            <ProtectedRoute>
              <EditSkillPage />
            </ProtectedRoute>
          }
        />

        {/* Skill DNA */}

        <Route
          path="/skill-dna"
          element={
            <ProtectedRoute>
              <SkillDNAPage />
            </ProtectedRoute>
          }
        />

        {/* Matching */}

        <Route
          path="/matching"
          element={
            <ProtectedRoute>
              <MatchingPage />
            </ProtectedRoute>
          }
        />

        {/* Global Student Discovery */}

        <Route
          path="/students"
          element={
            <ProtectedRoute>
              <StudentDiscoveryPage />
            </ProtectedRoute>
          }
        />

        {/* Student Full Profile */}

        <Route
          path="/students/:userId"
          element={
            <ProtectedRoute>
              <StudentProfilePage />
            </ProtectedRoute>
          }
        />

        {/* Student Message */}

        <Route
          path="/students/:userId/message"
          element={
            <ProtectedRoute>
              <MessagePage />
            </ProtectedRoute>
          }
        />

        {/* Voice / Video Call */}

        <Route
          path="/students/:userId/video-call"
          element={
            <ProtectedRoute>
              <VideoCallPage />
            </ProtectedRoute>
          }
        />

        {/* Skill Exchange */}

        <Route
          path="/exchange"
          element={
            <ProtectedRoute>
              <ExchangePage />
            </ProtectedRoute>
          }
        />

        {/* Existing Chat Module */}

        <Route
          path="/chat"
          element={
            <ProtectedRoute>
              <ChatPage />
            </ProtectedRoute>
          }
        />

        {/* Project Teams */}

        <Route
          path="/teams"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="Project Teams"
                description="Create teams, discover collaborators, manage invitations, and build project groups."
              />
            </ProtectedRoute>
          }
        />

        {/* Interview */}

        <Route
          path="/interview"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="Interview Preparation"
                description="Your interview preparation workspace will be available here."
              />
            </ProtectedRoute>
          }
        />

        {/* Career */}

        <Route
          path="/career"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="Career Intelligence"
                description="Career goals, skill gaps, roadmaps, resume intelligence, and career preparation will be available here."
              />
            </ProtectedRoute>
          }
        />

        {/* Projects */}

        <Route
          path="/projects"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="Projects"
                description="Discover projects, find collaborators, and build your campus project portfolio."
              />
            </ProtectedRoute>
          }
        />

        {/* AI */}

        <Route
          path="/ai"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="AI Assistant"
                description="Your AI-powered learning, skill, project, and career assistance will be available here."
              />
            </ProtectedRoute>
          }
        />

        {/* Settings */}

        <Route
          path="/settings"
          element={
            <ProtectedRoute>
              <ModulePlaceholder
                title="Settings"
                description="Manage your SkillSwap Campus application preferences and account settings."
              />
            </ProtectedRoute>
          }
        />

        {/* Fallback */}

        <Route
          path="*"
          element={
            <Navigate
              to="/"
              replace
            />
          }
        />

      </Routes>
    </BrowserRouter>
  );
};

export default AppRoutes;