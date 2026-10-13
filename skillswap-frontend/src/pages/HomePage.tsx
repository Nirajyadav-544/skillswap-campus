
import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const HomePage = () => {
  const { user, isAuthenticated, logout } = useAuth();

  const displayName =
    user?.firstName ||
    user?.email?.split("@")[0] ||
    "Student";

  const buttonPrimary =
    "inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2";

  const buttonSecondary =
    "inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-5 py-3 text-sm font-bold text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-slate-400 focus:ring-offset-2";

  const eyebrow =
    "inline-flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-indigo-600";

  return (
    <div className="min-h-screen overflow-x-hidden bg-white text-slate-900">
      {/* =====================================================
          NAVBAR
      ====================================================== */}
      <header className="sticky top-0 z-50 border-b border-slate-200/80 bg-white/90 backdrop-blur-xl">
        <div className="mx-auto flex h-[72px] max-w-7xl items-center justify-between gap-6 px-4 sm:px-6 lg:px-8">
          <Link
            to="/"
            className="flex shrink-0 items-center gap-3"
            aria-label="SkillSwap Campus home"
          >
            <span className="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 text-sm font-black tracking-tight text-white shadow-sm">
              SS
            </span>

            <span className="text-lg font-extrabold tracking-tight text-slate-900">
              SkillSwap
              <span className="block text-xs font-semibold tracking-[0.14em] text-indigo-600">
                Campus
              </span>
            </span>
          </Link>

          <nav className="hidden items-center gap-8 lg:flex">
            <a
              href="#platform"
              className="text-sm font-medium text-slate-600 transition hover:text-indigo-600"
            >
              Platform
            </a>

            <a
              href="#how-it-works"
              className="text-sm font-medium text-slate-600 transition hover:text-indigo-600"
            >
              How it works
            </a>

            <a
              href="#intelligence"
              className="text-sm font-medium text-slate-600 transition hover:text-indigo-600"
            >
              AI Intelligence
            </a>

            <a
              href="#community"
              className="text-sm font-medium text-slate-600 transition hover:text-indigo-600"
            >
              Community
            </a>
          </nav>

          <div className="flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <span className="hidden text-sm font-semibold text-slate-600 sm:inline">
                  Hi, {displayName}
                </span>

                <Link
                  to="/dashboard"
                  className={`${buttonPrimary} px-4 py-2.5`}
                >
                  Dashboard
                </Link>

                <button
                  type="button"
                  className="hidden rounded-xl px-4 py-2.5 text-sm font-bold text-slate-600 transition hover:bg-slate-100 hover:text-slate-900 sm:inline-flex"
                  onClick={logout}
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="hidden text-sm font-bold text-slate-600 transition hover:text-indigo-600 sm:inline"
                >
                  Login
                </Link>

                <Link
                  to="/register"
                  className={`${buttonPrimary} px-4 py-2.5`}
                >
                  Get Started
                </Link>
              </>
            )}
          </div>
        </div>
      </header>

      <main>
        {/* =====================================================
            HERO
        ====================================================== */}
        <section className="relative overflow-hidden bg-slate-50">
          <div className="pointer-events-none absolute -left-40 top-10 h-96 w-96 rounded-full bg-indigo-200/40 blur-3xl" />
          <div className="pointer-events-none absolute -right-40 top-40 h-[500px] w-[500px] rounded-full bg-violet-200/40 blur-3xl" />

          <div className="relative mx-auto grid max-w-7xl items-center gap-14 px-4 py-20 sm:px-6 sm:py-24 lg:grid-cols-[1.05fr_0.95fr] lg:px-8 lg:py-28">
            <div>
              <div className={eyebrow}>
                <span className="h-2 w-2 rounded-full bg-indigo-600" />
                Campus Skill & Career Intelligence
              </div>

              <h1 className="mt-6 max-w-3xl text-5xl font-black leading-[1.02] tracking-[-0.04em] text-slate-950 sm:text-6xl lg:text-7xl">
                Learn from people.
                <br />
                <span className="text-indigo-600">Build your future.</span>
              </h1>

              <p className="mt-7 max-w-2xl text-lg leading-8 text-slate-600 sm:text-xl">
                SkillSwap Campus connects students through skills, learning,
                projects and opportunities — powered by intelligent matching.
              </p>

              <div className="mt-9 flex flex-col gap-3 sm:flex-row">
                <Link
                  to={isAuthenticated ? "/dashboard" : "/register"}
                  className={`${buttonPrimary} px-6 py-3.5`}
                >
                  {isAuthenticated ? "Open Dashboard" : "Create Free Account"}
                  <span className="text-lg">→</span>
                </Link>

                <a
                  href="#platform"
                  className={`${buttonSecondary} px-6 py-3.5`}
                >
                  Explore Platform
                </a>
              </div>

              <div className="mt-10 flex items-center gap-4">
                <div className="flex -space-x-2">
                  {["R", "A", "S", "K"].map((letter) => (
                    <span
                      key={letter}
                      className="grid h-9 w-9 place-items-center rounded-full border-2 border-slate-50 bg-white text-xs font-bold text-slate-700 shadow-sm"
                    >
                      {letter}
                    </span>
                  ))}
                </div>

                <div>
                  <strong className="block text-sm font-bold text-slate-800">
                    Built for campus collaboration
                  </strong>

                  <small className="mt-0.5 block text-xs text-slate-500">
                    Skills • Learning • Projects • Careers
                  </small>
                </div>
              </div>
            </div>

            {/* =================================================
                SKILL DNA CARD
            ================================================== */}
            <div className="relative mx-auto w-full max-w-xl lg:ml-auto">
              <div className="pointer-events-none absolute -inset-8 rounded-full border border-indigo-200/60" />
              <div className="pointer-events-none absolute -inset-16 rounded-full border border-indigo-100/70" />

              <div className="relative rounded-3xl border border-slate-200 bg-white p-6 shadow-[0_25px_70px_rgba(15,23,42,0.12)] sm:p-8">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <span className="text-[10px] font-extrabold tracking-[0.2em] text-indigo-600">
                      YOUR SKILL DNA
                    </span>

                    <h2 className="mt-1 text-xl font-extrabold text-slate-900">
                      Growing profile
                    </h2>
                  </div>

                  <div className="grid h-10 w-10 place-items-center rounded-xl bg-indigo-50 text-lg text-indigo-600">
                    ✦
                  </div>
                </div>

                <div className="mt-7">
                  <div className="flex items-end gap-1">
                    <strong className="text-6xl font-black tracking-tight text-slate-950">
                      78
                    </strong>

                    <span className="mb-2 text-lg font-semibold text-slate-400">
                      /100
                    </span>
                  </div>

                  <small className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Skill strength
                  </small>
                </div>

                <div className="mt-7 space-y-5">
                  {[
                    ["Java", "86%"],
                    ["React", "74%"],
                    ["Problem Solving", "68%"],
                    ["Communication", "81%"],
                  ].map(([skill, percentage]) => (
                    <div key={skill}>
                      <div className="mb-2 flex items-center justify-between text-sm">
                        <span className="font-semibold text-slate-700">
                          {skill}
                        </span>

                        <strong className="text-indigo-600">
                          {percentage}
                        </strong>
                      </div>

                      <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                        <span
                          className="block h-full rounded-full bg-indigo-600"
                          style={{ width: percentage }}
                        />
                      </div>
                    </div>
                  ))}
                </div>

                <div className="mt-7 flex gap-3 rounded-2xl border border-indigo-100 bg-indigo-50 p-4">
                  <div className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-white text-indigo-600 shadow-sm">
                    ✦
                  </div>

                  <div>
                    <span className="text-[10px] font-extrabold tracking-[0.16em] text-indigo-600">
                      AI INSIGHT
                    </span>

                    <p className="mt-1 text-sm leading-5 text-slate-600">
                      Your profile is ready to discover relevant skill
                      partners.
                    </p>
                  </div>
                </div>
              </div>

              <div className="absolute -right-4 top-10 hidden items-center gap-3 rounded-2xl border border-slate-200 bg-white p-3 shadow-xl sm:flex">
                <div className="grid h-10 w-10 place-items-center rounded-xl bg-emerald-50 font-bold text-emerald-600">
                  ✓
                </div>

                <div>
                  <strong className="block text-sm font-bold text-slate-800">
                    Smart Match
                  </strong>

                  <span className="text-xs text-slate-500">
                    92% compatibility
                  </span>
                </div>
              </div>

              <div className="absolute -bottom-5 -left-4 hidden items-center gap-3 rounded-2xl border border-slate-200 bg-white p-3 shadow-xl sm:flex">
                <span className="grid h-10 w-10 place-items-center rounded-xl bg-orange-50 text-orange-600">
                  ◈
                </span>

                <div>
                  <strong className="block text-sm font-bold text-slate-800">
                    Project ready
                  </strong>

                  <span className="text-xs text-slate-500">
                    Find your team
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* =====================================================
            STATS
        ====================================================== */}
        <section className="border-y border-slate-200 bg-white">
          <div className="mx-auto grid max-w-7xl grid-cols-2 divide-x divide-slate-200 px-4 sm:px-6 lg:grid-cols-4 lg:px-8">
            {[
              ["01", "Skill-first identity"],
              ["AI", "Intelligent recommendations"],
              ["360°", "Learning to career ecosystem"],
              ["∞", "Opportunities to collaborate"],
            ].map(([value, label]) => (
              <div
                key={value}
                className="flex min-h-28 flex-col justify-center px-4 py-6 first:pl-0 lg:px-8"
              >
                <strong className="text-2xl font-black tracking-tight text-slate-950">
                  {value}
                </strong>

                <span className="mt-1 text-xs font-medium leading-5 text-slate-500 sm:text-sm">
                  {label}
                </span>
              </div>
            ))}
          </div>
        </section>

        {/* =====================================================
            PLATFORM
        ====================================================== */}
        <section id="platform" className="bg-white py-20 sm:py-24">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl">
              <div className={eyebrow}>
                <span className="h-2 w-2 rounded-full bg-indigo-600" />
                One connected ecosystem
              </div>

              <h2 className="mt-5 text-4xl font-black leading-tight tracking-tight text-slate-950 sm:text-5xl">
                Everything students need
                <br />
                to <span className="text-indigo-600">move forward.</span>
              </h2>

              <p className="mt-5 text-base leading-7 text-slate-600 sm:text-lg">
                Your profile, skills, learning journey, collaboration and
                career growth belong in one connected system.
              </p>
            </div>

            <div className="mt-12 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
              <article className="rounded-3xl border border-slate-200 bg-slate-50 p-7 shadow-sm transition hover:-translate-y-1 hover:shadow-lg lg:col-span-2">
                <div className="flex items-start justify-between">
                  <div className="grid h-12 w-12 place-items-center rounded-2xl bg-indigo-100 text-xl text-indigo-600">
                    ◇
                  </div>

                  <span className="text-xs font-bold text-slate-400">
                    01
                  </span>
                </div>

                <h3 className="mt-8 text-2xl font-extrabold text-slate-950">
                  Skill DNA
                </h3>

                <p className="mt-3 max-w-xl leading-7 text-slate-600">
                  Build a richer representation of what you know, what you can
                  teach and what you want to learn.
                </p>

                <div className="mt-6 flex flex-wrap gap-2">
                  {["Skills", "Interests", "Goals"].map((tag) => (
                    <span
                      key={tag}
                      className="rounded-full border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-600"
                    >
                      {tag}
                    </span>
                  ))}
                </div>
              </article>

              {[
                {
                  number: "02",
                  icon: "⇄",
                  title: "Skill Exchange",
                  text: "Teach what you know and discover students who can help you learn.",
                  iconClass: "bg-blue-100 text-blue-600",
                },
                {
                  number: "03",
                  icon: "✦",
                  title: "AI Matching",
                  text: "Discover relevant people based on skills, goals and collaboration needs.",
                  iconClass: "bg-emerald-100 text-emerald-600",
                },
                {
                  number: "04",
                  icon: "◈",
                  title: "Project Teams",
                  text: "Find complementary skills and bring stronger project teams together.",
                  iconClass: "bg-orange-100 text-orange-600",
                },
              ].map((feature) => (
                <article
                  key={feature.number}
                  className="rounded-3xl border border-slate-200 bg-white p-7 shadow-sm transition hover:-translate-y-1 hover:shadow-lg"
                >
                  <div className="flex items-start justify-between">
                    <div
                      className={`grid h-12 w-12 place-items-center rounded-2xl text-xl ${feature.iconClass}`}
                    >
                      {feature.icon}
                    </div>

                    <span className="text-xs font-bold text-slate-400">
                      {feature.number}
                    </span>
                  </div>

                  <h3 className="mt-8 text-xl font-extrabold text-slate-950">
                    {feature.title}
                  </h3>

                  <p className="mt-3 leading-7 text-slate-600">
                    {feature.text}
                  </p>
                </article>
              ))}

              <article className="relative overflow-hidden rounded-3xl bg-slate-950 p-7 text-white shadow-sm lg:col-span-2">
                <div className="absolute -right-20 -top-20 h-48 w-48 rounded-full bg-indigo-600/20 blur-2xl" />

                <div className="relative">
                  <div className="flex items-start justify-between">
                    <div className="grid h-12 w-12 place-items-center rounded-2xl bg-white/10 text-xl">
                      ↗
                    </div>

                    <span className="text-xs font-bold text-slate-500">
                      05
                    </span>
                  </div>

                  <h3 className="mt-8 text-2xl font-extrabold">
                    Career Intelligence
                  </h3>

                  <p className="mt-3 max-w-xl leading-7 text-slate-300">
                    Connect your skills and learning journey with future
                    projects and career goals.
                  </p>

                  <span className="mt-6 inline-flex text-xl">→</span>
                </div>
              </article>
            </div>
          </div>
        </section>

        {/* =====================================================
            HOW IT WORKS
        ====================================================== */}
        <section id="how-it-works" className="bg-slate-50 py-20 sm:py-24">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="mx-auto max-w-2xl text-center">
              <div className={`${eyebrow} justify-center`}>
                <span className="h-2 w-2 rounded-full bg-indigo-600" />
                How SkillSwap works
              </div>

              <h2 className="mt-5 text-4xl font-black leading-tight tracking-tight text-slate-950 sm:text-5xl">
                Your skills become
                <br />
                <span className="text-indigo-600">your network.</span>
              </h2>

              <p className="mt-5 text-base leading-7 text-slate-600 sm:text-lg">
                Start with what you know. Tell the platform what you want to
                learn. Then let the ecosystem connect the pieces.
              </p>
            </div>

            <div className="mt-14 grid gap-6 md:grid-cols-3">
              {[
                {
                  number: "01",
                  icon: "+",
                  title: "Create your profile",
                  text: "Add your skills, interests, goals and experience.",
                },
                {
                  number: "02",
                  icon: "◎",
                  title: "Discover matches",
                  text: "Find students whose skills complement your goals.",
                },
                {
                  number: "03",
                  icon: "↗",
                  title: "Learn & collaborate",
                  text: "Exchange skills, build projects and grow together.",
                },
              ].map((step) => (
                <div
                  key={step.number}
                  className="relative rounded-3xl border border-slate-200 bg-white p-7 shadow-sm"
                >
                  <span className="absolute right-6 top-6 text-xs font-bold text-slate-300">
                    {step.number}
                  </span>

                  <div className="grid h-14 w-14 place-items-center rounded-2xl bg-indigo-50 text-xl font-bold text-indigo-600">
                    {step.icon}
                  </div>

                  <h3 className="mt-7 text-xl font-extrabold text-slate-950">
                    {step.title}
                  </h3>

                  <p className="mt-3 leading-7 text-slate-600">
                    {step.text}
                  </p>

                  {step.number !== "03" && (
                    <span className="absolute -right-5 top-1/2 z-10 hidden text-2xl text-indigo-300 md:block">
                      →
                    </span>
                  )}
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* =====================================================
            AI INTELLIGENCE
        ====================================================== */}
        <section
          id="intelligence"
          className="overflow-hidden bg-slate-950 py-20 text-white sm:py-24"
        >
          <div className="mx-auto grid max-w-7xl items-center gap-14 px-4 sm:px-6 lg:grid-cols-2 lg:px-8">
            <div>
              <div className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-indigo-300">
                <span className="h-2 w-2 rounded-full bg-indigo-400" />
                Intelligence layer
              </div>

              <h2 className="mt-5 text-4xl font-black leading-tight tracking-tight sm:text-5xl">
                AI that understands
                <br />
                <span className="text-indigo-400">your potential.</span>
              </h2>

              <p className="mt-6 max-w-xl text-base leading-7 text-slate-300 sm:text-lg">
                SkillSwap is designed so AI supports the core campus
                experience — matching people, understanding skill gaps,
                guiding learning and helping students find opportunities.
              </p>

              <div className="mt-10 space-y-7">
                {[
                  {
                    number: "01",
                    title: "Smart skill matching",
                    text: "Connect complementary skills and learning goals.",
                  },
                  {
                    number: "02",
                    title: "Skill-gap intelligence",
                    text: "Understand what skills can help move a student toward a goal.",
                  },
                  {
                    number: "03",
                    title: "Personalized direction",
                    text: "Turn profile information into useful learning and career context.",
                  },
                ].map((item) => (
                  <div
                    key={item.number}
                    className="border-l border-slate-700 pl-5"
                  >
                    <span className="text-xs font-bold text-indigo-400">
                      {item.number}
                    </span>

                    <strong className="mt-1 block text-base font-bold">
                      {item.title}
                    </strong>

                    <p className="mt-1 text-sm leading-6 text-slate-400">
                      {item.text}
                    </p>
                  </div>
                ))}
              </div>
            </div>

            <div className="relative">
              <div className="rounded-3xl border border-slate-700 bg-slate-900 p-5 shadow-2xl sm:p-7">
                <div className="flex items-center justify-between">
                  <div>
                    <span className="text-[10px] font-extrabold tracking-[0.2em] text-indigo-400">
                      SKILL GRAPH
                    </span>

                    <strong className="mt-1 block text-lg font-extrabold">
                      Your learning network
                    </strong>
                  </div>

                  <span className="inline-flex items-center gap-2 rounded-full bg-emerald-400/10 px-3 py-1.5 text-xs font-bold text-emerald-400">
                    <span className="h-2 w-2 rounded-full bg-emerald-400" />
                    Live
                  </span>
                </div>

                <div className="relative mt-8 h-[330px] overflow-hidden rounded-2xl bg-slate-950">
                  <span className="absolute left-1/2 top-1/2 h-px w-[80%] -translate-x-1/2 -rotate-12 bg-slate-700" />
                  <span className="absolute left-1/2 top-1/2 h-px w-[75%] -translate-x-1/2 rotate-45 bg-slate-700" />
                  <span className="absolute left-1/2 top-1/2 h-px w-[70%] -translate-x-1/2 rotate-[110deg] bg-slate-700" />
                  <span className="absolute left-1/2 top-1/2 h-px w-[65%] -translate-x-1/2 -rotate-[65deg] bg-slate-700" />

                  <div className="absolute left-1/2 top-1/2 grid h-20 w-20 -translate-x-1/2 -translate-y-1/2 place-items-center rounded-full border border-indigo-400/50 bg-indigo-600 text-xs font-black shadow-[0_0_40px_rgba(99,102,241,0.35)]">
                    YOU
                  </div>

                  <div className="absolute left-[14%] top-[20%] rounded-full border border-slate-700 bg-slate-900 px-4 py-2 text-xs font-bold text-slate-300">
                    React
                  </div>

                  <div className="absolute right-[12%] top-[22%] rounded-full border border-slate-700 bg-slate-900 px-4 py-2 text-xs font-bold text-slate-300">
                    Java
                  </div>

                  <div className="absolute left-[12%] bottom-[22%] rounded-full border border-slate-700 bg-slate-900 px-4 py-2 text-xs font-bold text-slate-300">
                    AI
                  </div>

                  <div className="absolute right-[15%] bottom-[18%] rounded-full border border-slate-700 bg-slate-900 px-4 py-2 text-xs font-bold text-slate-300">
                    Team
                  </div>

                  <div className="absolute left-1/2 bottom-[8%] -translate-x-1/2 rounded-full border border-indigo-500/30 bg-indigo-500/10 px-4 py-2 text-xs font-bold text-indigo-300">
                    Career
                  </div>
                </div>

                <div className="mt-5 flex items-center justify-between text-xs font-semibold text-slate-500">
                  <span>06 connected areas</span>
                  <span>Skill intelligence</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* =====================================================
            COMMUNITY
        ====================================================== */}
        <section id="community" className="bg-white py-20 sm:py-24">
          <div className="mx-auto grid max-w-7xl items-center gap-12 px-4 sm:px-6 lg:grid-cols-2 lg:px-8">
            <div className="rounded-[2rem] bg-indigo-600 p-8 text-white shadow-xl sm:p-10">
              <div className="flex items-center justify-between gap-4">
                <span className="text-xs font-extrabold tracking-[0.16em] text-indigo-100">
                  CAMPUS COMMUNITY
                </span>

                <span className="text-xs font-bold text-indigo-100">
                  ● Growing
                </span>
              </div>

              <h3 className="mt-10 text-3xl font-black leading-tight tracking-tight sm:text-4xl">
                Knowledge becomes more
                <span className="text-indigo-200"> valuable when shared.</span>
              </h3>

              <p className="mt-5 leading-7 text-indigo-100">
                Students do not have to learn alone. SkillSwap creates a
                structured space where knowledge can move between people.
              </p>

              <div className="mt-8 flex flex-wrap gap-2">
                {["Learn", "Teach", "Build", "Connect"].map((item) => (
                  <span
                    key={item}
                    className="rounded-full border border-white/20 bg-white/10 px-4 py-2 text-xs font-bold"
                  >
                    {item}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <div className={eyebrow}>
                <span className="h-2 w-2 rounded-full bg-indigo-600" />
                More than a profile
              </div>

              <h2 className="mt-5 text-4xl font-black leading-tight tracking-tight text-slate-950 sm:text-5xl">
                Turn your campus
                <br />
                into a <span className="text-indigo-600">skill network.</span>
              </h2>

              <p className="mt-6 max-w-xl text-base leading-7 text-slate-600 sm:text-lg">
                A student profile should not be a static resume. It should
                become a living map of skills, learning goals, collaboration
                and progress.
              </p>

              <Link
                to={isAuthenticated ? "/dashboard" : "/register"}
                className="mt-8 inline-flex items-center gap-2 text-sm font-extrabold text-indigo-600 transition hover:gap-3"
              >
                {isAuthenticated
                  ? "Go to your dashboard"
                  : "Start building your profile"}
                <span className="text-lg">→</span>
              </Link>
            </div>
          </div>
        </section>

        {/* =====================================================
            FINAL CTA
        ====================================================== */}
        <section className="relative overflow-hidden bg-indigo-600 py-20 text-white sm:py-24">
          <div className="pointer-events-none absolute -left-20 -top-32 h-80 w-80 rounded-full border border-white/10" />
          <div className="pointer-events-none absolute -bottom-40 -right-20 h-96 w-96 rounded-full border border-white/10" />

          <div className="relative mx-auto max-w-4xl px-4 text-center sm:px-6">
            <div className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-indigo-100">
              <span className="h-2 w-2 rounded-full bg-white" />
              Your next opportunity can start here
            </div>

            <h2 className="mt-6 text-4xl font-black leading-[1.05] tracking-tight sm:text-6xl">
              Learn something.
              <br />
              <span className="text-indigo-200">Teach something.</span>
              <br />
              Build something.
            </h2>

            <p className="mx-auto mt-6 max-w-2xl text-base leading-7 text-indigo-100 sm:text-lg">
              Join a campus ecosystem designed around skills, people and
              meaningful collaboration.
            </p>

            <div className="mt-9 flex flex-col justify-center gap-3 sm:flex-row">
              <Link
                to={isAuthenticated ? "/dashboard" : "/register"}
                className="inline-flex items-center justify-center gap-2 rounded-xl bg-white px-6 py-3.5 text-sm font-extrabold text-indigo-700 shadow-lg transition hover:bg-indigo-50 focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-indigo-600"
              >
                {isAuthenticated ? "Open Dashboard" : "Get Started Free"}
                <span className="text-lg">→</span>
              </Link>

              {!isAuthenticated && (
                <Link
                  to="/login"
                  className="inline-flex items-center justify-center rounded-xl border border-white/40 bg-white/10 px-6 py-3.5 text-sm font-extrabold text-white transition hover:bg-white/15 focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-indigo-600"
                >
                  I already have an account
                </Link>
              )}
            </div>
          </div>
        </section>
      </main>

      {/* =====================================================
          FOOTER
      ====================================================== */}
      <footer className="border-t border-slate-200 bg-white">
        <div className="mx-auto grid max-w-7xl gap-12 px-4 py-14 sm:px-6 md:grid-cols-[1.3fr_1fr] lg:px-8">
          <div className="max-w-sm">
            <Link to="/" className="flex items-center gap-3">
              <span className="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 text-sm font-black text-white">
                SS
              </span>

              <span className="text-lg font-extrabold tracking-tight text-slate-900">
                SkillSwap
                <span className="block text-xs font-semibold tracking-[0.14em] text-indigo-600">
                  Campus
                </span>
              </span>
            </Link>

            <p className="mt-5 text-sm leading-6 text-slate-500">
              A connected campus ecosystem for skills, learning, collaboration
              and growth.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-8 sm:grid-cols-3">
            <div className="flex flex-col gap-3">
              <strong className="text-sm font-extrabold text-slate-900">
                Explore
              </strong>

              <a
                href="#platform"
                className="text-sm text-slate-500 transition hover:text-indigo-600"
              >
                Platform
              </a>

              <a
                href="#how-it-works"
                className="text-sm text-slate-500 transition hover:text-indigo-600"
              >
                How it works
              </a>

              <a
                href="#intelligence"
                className="text-sm text-slate-500 transition hover:text-indigo-600"
              >
                AI Intelligence
              </a>
            </div>

            <div className="flex flex-col gap-3">
              <strong className="text-sm font-extrabold text-slate-900">
                Account
              </strong>

              {isAuthenticated ? (
                <>
                  <Link
                    to="/dashboard"
                    className="text-sm text-slate-500 transition hover:text-indigo-600"
                  >
                    Dashboard
                  </Link>

                  <Link
                    to="/profile"
                    className="text-sm text-slate-500 transition hover:text-indigo-600"
                  >
                    Profile
                  </Link>
                </>
              ) : (
                <>
                  <Link
                    to="/login"
                    className="text-sm text-slate-500 transition hover:text-indigo-600"
                  >
                    Login
                  </Link>

                  <Link
                    to="/register"
                    className="text-sm text-slate-500 transition hover:text-indigo-600"
                  >
                    Register
                  </Link>
                </>
              )}
            </div>

            <div className="flex flex-col gap-3">
              <strong className="text-sm font-extrabold text-slate-900">
                Vision
              </strong>

              <span className="text-sm text-slate-500">Skills</span>
              <span className="text-sm text-slate-500">Collaboration</span>
              <span className="text-sm text-slate-500">Career</span>
            </div>
          </div>
        </div>

        <div className="border-t border-slate-200">
          <div className="mx-auto flex max-w-7xl flex-col gap-2 px-4 py-5 text-xs font-medium text-slate-400 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
            <span>© 2026 SkillSwap Campus</span>
            <span>Learn • Exchange • Build</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;

