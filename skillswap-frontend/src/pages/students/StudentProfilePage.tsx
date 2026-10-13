import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import chatService from "../../services/chat/chatService";
import studentService from "../../services/student/studentService";

import {
  getStudentName,
  type StudentProfile,
} from "../../types/student/student.types";

const StudentProfilePage = () => {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [student, setStudent] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [messageLoading, setMessageLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;

    const loadProfile = async () => {
      if (!userId) {
        setError("Student ID is missing.");
        setLoading(false);
        return;
      }

      const parsedUserId = Number(userId);

      if (!Number.isInteger(parsedUserId) || parsedUserId <= 0) {
        setError("Invalid student ID.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const response =
          await studentService.getStudentProfile(parsedUserId);

        if (mounted) {
          setStudent(response);
        }
      } catch (err) {
        console.error("Failed to load student profile:", err);

        if (mounted) {
          setError("Unable to load this student profile.");
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    void loadProfile();

    return () => {
      mounted = false;
    };
  }, [userId]);

  const handleMessage = async () => {
    if (!student) {
      return;
    }

    if (!user?.email) {
      setError(
        "Your account information is unavailable. Please log in again."
      );
      return;
    }

    if (student.userId === user.id) {
      setError("You cannot start a conversation with yourself.");
      return;
    }

    try {
      setMessageLoading(true);
      setError("");

      await chatService.createOrGetDirectConversation(
        student.userId,
        user.email
      );

      navigate(`/students/${student.userId}/message`);
    } catch (err) {
      console.error(
        "Failed to open direct conversation:",
        err
      );

      setError(
        "Unable to open the conversation. Please try again."
      );
    } finally {
      setMessageLoading(false);
    }
  };

  const handleVoiceCall = async () => {
    if (!student) {
      return;
    }

    if (!user?.email) {
      setError(
        "Your account information is unavailable. Please log in again."
      );
      return;
    }

    if (student.userId === user.id) {
      setError("You cannot call yourself.");
      return;
    }

    try {
      setMessageLoading(true);
      setError("");

      const conversation =
        await chatService.createOrGetDirectConversation(
          student.userId,
          user.email
        );

      navigate(
        `/students/${student.userId}/video-call?conversationId=${conversation.id}&mode=voice&initiator=true`
      );
    } catch (err) {
      console.error(
        "Failed to start voice call:",
        err
      );

      setError(
        "Unable to start the voice call. Please try again."
      );
    } finally {
      setMessageLoading(false);
    }
  };

  const handleVideoCall = async () => {
    if (!student) {
      return;
    }

    if (!user?.email) {
      setError(
        "Your account information is unavailable. Please log in again."
      );
      return;
    }

    if (student.userId === user.id) {
      setError("You cannot call yourself.");
      return;
    }

    try {
      setMessageLoading(true);
      setError("");

      const conversation =
        await chatService.createOrGetDirectConversation(
          student.userId,
          user.email
        );

      navigate(
        `/students/${student.userId}/video-call?conversationId=${conversation.id}&mode=video&initiator=true`
      );
    } catch (err) {
      console.error(
        "Failed to start video call:",
        err
      );

      setError(
        "Unable to start the video call. Please try again."
      );
    } finally {
      setMessageLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-[70vh] items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-slate-900" />

          <p className="text-sm text-slate-500">
            Loading student profile...
          </p>
        </div>
      </div>
    );
  }

  if (error && !student) {
    return (
      <div className="min-h-[70vh] px-4 py-10">
        <div className="mx-auto max-w-2xl rounded-2xl border border-red-200 bg-red-50 p-8 text-center">
          <div className="mb-3 text-4xl">
            ⚠️
          </div>

          <h1 className="text-xl font-bold text-red-900">
            Profile unavailable
          </h1>

          <p className="mt-2 text-sm text-red-700">
            {error}
          </p>

          <button
            type="button"
            onClick={() => navigate("/students")}
            className="mt-6 rounded-xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800"
          >
            Back to Students
          </button>
        </div>
      </div>
    );
  }

  if (!student) {
    return null;
  }

  const name = getStudentName(student);

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-5xl">

        {/* Back */}
        <button
          type="button"
          onClick={() => navigate("/students")}
          className="mb-6 text-sm font-semibold text-slate-600 transition hover:text-indigo-600"
        >
          ← Back to Students
        </button>

        {/* Profile Header */}
        <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">

          {/* Cover */}
          <div className="h-36 bg-gradient-to-r from-indigo-700 via-violet-700 to-purple-700 sm:h-48" />

          <div className="relative px-5 pb-7 sm:px-8">

            {/* Avatar + Actions */}
            <div className="-mt-16 flex flex-col gap-5 sm:-mt-20 sm:flex-row sm:items-end sm:justify-between">

              <div>
                {student.profileImageUrl ? (
                  <img
                    src={student.profileImageUrl}
                    alt={name}
                    className="h-32 w-32 rounded-3xl border-4 border-white bg-white object-cover shadow-lg"
                  />
                ) : (
                  <div className="flex h-32 w-32 items-center justify-center rounded-3xl border-4 border-white bg-slate-100 text-4xl font-black text-slate-500 shadow-lg">
                    {name.charAt(0).toUpperCase()}
                  </div>
                )}
              </div>

              <div className="flex flex-wrap gap-3">

                {/* Message */}
                <button
                  type="button"
                  onClick={handleMessage}
                  disabled={messageLoading}
                  className="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {messageLoading
                    ? "Opening..."
                    : "💬 Message"}
                </button>

                {/* Audio Call */}
                <button
                  type="button"
                  onClick={handleVoiceCall}
                  disabled={messageLoading}
                  className="rounded-xl border border-emerald-200 bg-white px-5 py-3 text-sm font-bold text-emerald-700 transition hover:border-emerald-300 hover:bg-emerald-50 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  📞 Audio Call
                </button>

                {/* Video Call */}
                <button
                  type="button"
                  onClick={handleVideoCall}
                  disabled={messageLoading}
                  className="rounded-xl border border-purple-200 bg-white px-5 py-3 text-sm font-bold text-purple-700 transition hover:border-purple-300 hover:bg-purple-50 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  📹 Video Call
                </button>

              </div>
            </div>

            {/* Name / Headline */}
            <div className="mt-6">

              <div className="flex flex-wrap items-center gap-3">

                <h1 className="text-3xl font-black tracking-tight text-slate-900">
                  {name}
                </h1>

                {student.emailVerified && (
                  <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">
                    ✓ Verified
                  </span>
                )}

              </div>

              {student.headline && (
                <p className="mt-2 text-lg text-slate-600">
                  {student.headline}
                </p>
              )}

              {student.location && (
                <p className="mt-2 text-sm text-slate-500">
                  📍 {student.location}
                </p>
              )}

            </div>
          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="mt-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        {/* Profile Content */}
        <div className="mt-6 grid gap-6 lg:grid-cols-3">

          {/* Main */}
          <div className="space-y-6 lg:col-span-2">

            {/* About */}
            {student.bio && (
              <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">

                <h2 className="text-lg font-bold text-slate-900">
                  About
                </h2>

                <p className="mt-3 whitespace-pre-line text-sm leading-7 text-slate-600">
                  {student.bio}
                </p>

              </section>
            )}

            {/* Education */}
            <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">

              <h2 className="text-lg font-bold text-slate-900">
                Education
              </h2>

              <div className="mt-5 grid gap-5 sm:grid-cols-2">

                <InfoItem
                  label="College"
                  value={student.college}
                />

                <InfoItem
                  label="Department"
                  value={student.department}
                />

                <InfoItem
                  label="Course"
                  value={student.course}
                />

                <InfoItem
                  label="Year of Study"
                  value={
                    student.yearOfStudy
                      ? String(student.yearOfStudy)
                      : null
                  }
                />

                <InfoItem
                  label="Graduation Year"
                  value={
                    student.graduationYear
                      ? String(student.graduationYear)
                      : null
                  }
                />

                <InfoItem
                  label="Location"
                  value={student.location}
                />

              </div>
            </section>

          </div>

          {/* Sidebar */}
          <aside className="space-y-6">

            {/* Availability */}
            <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">

              <h2 className="text-lg font-bold text-slate-900">
                Availability
              </h2>

              <p className="mt-3 text-sm leading-6 text-slate-600">
                {student.availability ||
                  "Availability information not provided."}
              </p>

            </section>

            {/* Links */}
            <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">

              <h2 className="text-lg font-bold text-slate-900">
                Links
              </h2>

              <div className="mt-4 space-y-3">

                <ProfileLink
                  label="GitHub"
                  url={student.githubUrl}
                />

                <ProfileLink
                  label="LinkedIn"
                  url={student.linkedinUrl}
                />

                <ProfileLink
                  label="Portfolio"
                  url={student.portfolioUrl}
                />

              </div>

            </section>

          </aside>
        </div>
      </div>
    </div>
  );
};

interface InfoItemProps {
  label: string;
  value?: string | null;
}

const InfoItem = ({
  label,
  value,
}: InfoItemProps) => {
  return (
    <div>
      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {label}
      </p>

      <p className="mt-1 text-sm font-semibold text-slate-800">
        {value || "Not provided"}
      </p>
    </div>
  );
};

interface ProfileLinkProps {
  label: string;
  url?: string | null;
}

const ProfileLink = ({
  label,
  url,
}: ProfileLinkProps) => {
  if (!url) {
    return (
      <div className="flex items-center justify-between text-sm">
        <span className="font-semibold text-slate-700">
          {label}
        </span>

        <span className="text-slate-400">
          Not provided
        </span>
      </div>
    );
  }

  return (
    <a
      href={url}
      target="_blank"
      rel="noreferrer"
      className="flex items-center justify-between text-sm font-semibold text-indigo-600 transition hover:text-indigo-800"
    >
      <span>{label}</span>
      <span>↗</span>
    </a>
  );
};

export default StudentProfilePage;