import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import AppShell from "../../components/layout/AppShell";
import { useAuth } from "../../hooks/useAuth";
import apiClient from "../../services/api/apiClient";

interface Activity {
  id: number;
  type: string;
  message: string;
  referenceId: number | null;
  referenceType: string | null;
  createdAt: string;
}

const DashboardPage = () => {
  const { user } = useAuth();

  const displayName =
    user?.firstName?.trim() ||
    "Student";

  const [activities, setActivities] =
    useState<Activity[]>([]);

  const [activityLoading, setActivityLoading] =
    useState(true);

  const [activityError, setActivityError] =
    useState("");

  useEffect(() => {
    const loadActivities = async () => {
      try {
        setActivityLoading(true);
        setActivityError("");
const response = await apiClient.get<Activity[]>(
  "/v1/activity/mine"
);

setActivities(response);
      } catch (error) {
        console.error(
          "Failed to load dashboard activities:",
          error
        );

        setActivityError(
          "Unable to load your recent activity."
        );
      } finally {
        setActivityLoading(false);
      }
    };

    loadActivities();
  }, []);

  const formatActivityTime = (
    createdAt: string
  ) => {
    const date = new Date(createdAt);

    if (Number.isNaN(date.getTime())) {
      return "";
    }

    return date.toLocaleString(undefined, {
      dateStyle: "medium",
      timeStyle: "short",
    });
  };

  return (
    <AppShell>
      <div className="min-h-full bg-slate-50">
        <section className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">

          {/* HEADER */}
          <header className="flex flex-col gap-5 border-b border-slate-200 pb-6 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="mb-2 text-sm font-semibold uppercase tracking-wide text-indigo-600">
                SkillSwap Campus
              </p>

              <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                Welcome back, {displayName} 👋
              </h1>

              <p className="mt-2 text-sm text-slate-600 sm:text-base">
                Your SkillSwap Campus intelligence dashboard.
              </p>
            </div>

            <Link
              to="/profile"
              className="inline-flex w-full items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 md:w-auto"
            >
              View Profile
            </Link>
          </header>

          {/* STATISTICS */}
          <section
            className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4"
            aria-label="Dashboard statistics"
          >
            <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
              <div className="text-sm font-medium text-slate-500">
                Skills
              </div>

              <p className="mt-3 text-3xl font-bold text-slate-900">
                0
              </p>

              <p className="mt-1 text-xs text-slate-500">
                Skills in your profile
              </p>
            </article>

            <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
              <div className="text-sm font-medium text-slate-500">
                Exchange Requests
              </div>

              <p className="mt-3 text-3xl font-bold text-slate-900">
                0
              </p>

              <p className="mt-1 text-xs text-slate-500">
                Active exchange requests
              </p>
            </article>

            <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
              <div className="text-sm font-medium text-slate-500">
                Teams
              </div>

              <p className="mt-3 text-3xl font-bold text-slate-900">
                0
              </p>

              <p className="mt-1 text-xs text-slate-500">
                Teams you are part of
              </p>
            </article>

            <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
              <div className="text-sm font-medium text-slate-500">
                Profile Completion
              </div>

              <p className="mt-3 text-3xl font-bold text-slate-900">
                0%
              </p>

              <p className="mt-1 text-xs text-slate-500">
                Complete your profile
              </p>
            </article>
          </section>

          {/* ACTIVITY + QUICK ACTIONS */}
          <section className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">

            {/* ACTIVITY */}
            <article className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
              <header className="border-b border-slate-200 px-6 py-5">
                <h2 className="text-lg font-semibold text-slate-900">
                  SkillSwap Activity
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Keep track of your recent activity.
                </p>
              </header>

              {activityLoading ? (
                <div className="flex min-h-[300px] items-center justify-center px-6 py-10">
                  <div className="text-center">
                    <div className="mx-auto h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

                    <p className="mt-4 text-sm text-slate-500">
                      Loading your activity...
                    </p>
                  </div>
                </div>
              ) : activityError ? (
                <div className="flex min-h-[300px] flex-col items-center justify-center px-6 py-10 text-center">
                  <div className="flex h-14 w-14 items-center justify-center rounded-full bg-red-50 text-xl text-red-600">
                    !
                  </div>

                  <h3 className="mt-5 text-base font-semibold text-slate-900">
                    Activity unavailable
                  </h3>

                  <p className="mt-2 max-w-md text-sm leading-6 text-slate-500">
                    {activityError}
                  </p>
                </div>
              ) : activities.length === 0 ? (
                <div className="flex min-h-[300px] flex-col items-center justify-center px-6 py-10 text-center">
                  <div className="flex h-14 w-14 items-center justify-center rounded-full bg-indigo-50 text-2xl text-indigo-600">
                    ↗
                  </div>

                  <h3 className="mt-5 text-base font-semibold text-slate-900">
                    Your activity will appear here
                  </h3>

                  <p className="mt-2 max-w-md text-sm leading-6 text-slate-500">
                    Once you start adding skills, creating exchanges,
                    joining teams, and using the platform, your recent
                    activity will be shown here.
                  </p>
                </div>
              ) : (
                <div className="divide-y divide-slate-100">
                  {activities.map((activity) => (
                    <div
                      key={activity.id}
                      className="flex gap-4 px-6 py-5 transition hover:bg-slate-50"
                    >
                      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo-50 text-sm font-bold text-indigo-600">
                        {activity.type === "SKILL_ADDED"
                          ? "+"
                          : activity.type === "SKILL_UPDATED"
                            ? "↻"
                            : activity.type === "SKILL_DELETED"
                              ? "−"
                              : "↗"}
                      </div>

                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-semibold text-slate-900">
                          {activity.message}
                        </p>

                        <p className="mt-1 text-xs text-slate-500">
                          {formatActivityTime(
                            activity.createdAt
                          )}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </article>

            {/* QUICK ACTIONS */}
            <article className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
              <header className="border-b border-slate-200 px-6 py-5">
                <h2 className="text-lg font-semibold text-slate-900">
                  Quick Actions
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Jump directly to important areas.
                </p>
              </header>

              <div className="flex flex-col gap-3 p-6">
                <Link
                  to="/profile"
                  className="w-full rounded-xl bg-indigo-600 px-4 py-3 text-center text-sm font-semibold text-white transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                >
                  Complete Profile
                </Link>

                <Link
                  to="/skills"
                  className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-center text-sm font-semibold text-slate-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                >
                  Explore Skills
                </Link>

                <Link
                  to="/exchange"
                  className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-center text-sm font-semibold text-slate-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                >
                  Find Skill Exchange
                </Link>

                <Link
                  to="/teams"
                  className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-center text-sm font-semibold text-slate-700 transition hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                >
                  Explore Teams
                </Link>
              </div>
            </article>
          </section>
        </section>
      </div>
    </AppShell>
  );
};

export default DashboardPage;