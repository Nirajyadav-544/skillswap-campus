
import { Link } from "react-router-dom";

import RegisterForm from "../../components/auth/RegisterForm";

const RegisterPage = () => {
  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-2xl flex-col items-center justify-center">
        <Link
          to="/"
          className="mb-6 inline-flex items-center gap-3 text-slate-900 no-underline"
        >
          <span className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-indigo-600 text-xs font-black tracking-tight text-white shadow-sm">
            SS
          </span>

          <span className="text-lg font-extrabold tracking-tight">
            SkillSwap Campus
          </span>
        </Link>

        <section className="w-full rounded-2xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8">
          <div className="mb-7 text-center">
            <span className="inline-flex rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1.5 text-[11px] font-bold uppercase tracking-wider text-indigo-700">
              Join the campus network
            </span>

            <h1 className="mt-5 text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">
              Create your account
            </h1>

            <p className="mx-auto mt-3 max-w-lg text-sm leading-6 text-slate-500 sm:text-base">
              Build your Skill DNA, exchange knowledge, connect with
              students, and grow your career profile.
            </p>
          </div>

          <RegisterForm />

          <p className="mt-7 text-center text-sm text-slate-500">
            Already have an account?{" "}
            <Link
              to="/login"
              className="font-semibold text-indigo-600 transition hover:text-indigo-700 hover:underline"
            >
              Login
            </Link>
          </p>
        </section>

        <Link
          to="/"
          className="mt-6 text-sm font-medium text-slate-500 transition hover:text-indigo-600"
        >
          ← Back to SkillSwap Campus
        </Link>
      </div>
    </main>
  );
};

export default RegisterPage;

