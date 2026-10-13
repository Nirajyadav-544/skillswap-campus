import { useEffect, useState } from "react";

import AppShell from "../../components/layout/AppShell";

import skillService from "../../services/skill/skillService";

import exchangeService, {
  type CreateExchangeRequest,
  type ExchangeRequestResponse,
} from "../../services/exchange/exchangeService";

import type {
  SkillSummary,
  UserSkillSummary,
} from "../../types/skill/skill.types";

type ExchangeTab =
  | "discover"
  | "sent"
  | "received";

const ExchangePage = () => {
  // =========================================================
  // DISCOVERY STATE
  // =========================================================

  const [activeTab, setActiveTab] =
    useState<ExchangeTab>("discover");

  const [query, setQuery] = useState("");

  const [skills, setSkills] =
    useState<SkillSummary[]>([]);

  const [selectedSkill, setSelectedSkill] =
    useState<SkillSummary | null>(null);

  const [teachers, setTeachers] =
    useState<UserSkillSummary[]>([]);

  const [searchLoading, setSearchLoading] =
    useState(false);

  const [teachersLoading, setTeachersLoading] =
    useState(false);

  // =========================================================
  // REQUEST STATE
  // =========================================================

  const [sentRequests, setSentRequests] =
    useState<ExchangeRequestResponse[]>([]);

  const [receivedRequests, setReceivedRequests] =
    useState<ExchangeRequestResponse[]>([]);

  const [requestsLoading, setRequestsLoading] =
    useState(false);

  const [actionLoading, setActionLoading] =
    useState<number | null>(null);

  // =========================================================
  // MODAL STATE
  // =========================================================

  const [selectedTeacher, setSelectedTeacher] =
    useState<UserSkillSummary | null>(null);

  const [requestMessage, setRequestMessage] =
    useState("");

  const [offeredSkillName, setOfferedSkillName] =
    useState("");

  const [sendingRequest, setSendingRequest] =
    useState(false);

  // =========================================================
  // GENERAL STATE
  // =========================================================

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {
    loadRequests();
  }, []);

  // =========================================================
  // SEARCH SKILLS
  // =========================================================

  useEffect(() => {
    const trimmedQuery = query.trim();

    if (!trimmedQuery || selectedSkill) {
      setSkills([]);
      return;
    }

    const timeout = window.setTimeout(
      async () => {
        try {
          setSearchLoading(true);
          setError("");

          const result =
            await skillService.searchSkills(
              trimmedQuery,
            );

          setSkills(result);
        } catch (err) {
          console.error(
            "Failed to search skills:",
            err,
          );

          setSkills([]);

          setError(
            "Unable to search skills.",
          );
        } finally {
          setSearchLoading(false);
        }
      },
      350,
    );

    return () => {
      window.clearTimeout(timeout);
    };
  }, [query, selectedSkill]);

  // =========================================================
  // LOAD SENT + RECEIVED REQUESTS
  // =========================================================

  const loadRequests = async () => {
    try {
      setRequestsLoading(true);
      setError("");

      const [sent, received] =
        await Promise.all([
          exchangeService.getSentRequests(),
          exchangeService.getReceivedRequests(),
        ]);

      setSentRequests(sent);
      setReceivedRequests(received);
    } catch (err) {
      console.error(
        "Failed to load exchange requests:",
        err,
      );

      setError(
        "Unable to load exchange requests.",
      );
    } finally {
      setRequestsLoading(false);
    }
  };

  // =========================================================
  // FIND TEACHERS
  // =========================================================

  const findTeachers = async (
    skill: SkillSummary,
  ) => {
    try {
      setSelectedSkill(skill);
      setQuery(skill.name);
      setSkills([]);
      setTeachers([]);
      setTeachersLoading(true);

      setError("");
      setSuccess("");
    } catch (err) {
      console.error(err);
    }

    try {
      const result =
        await skillService.getTeachersForSkill(
          skill.id,
        );

      setTeachers(result);
    } catch (err) {
      console.error(
        "Failed to find teachers:",
        err,
      );

      setTeachers([]);

      setError(
        "Unable to find students who teach this skill.",
      );
    } finally {
      setTeachersLoading(false);
    }
  };

  // =========================================================
  // OPEN REQUEST MODAL
  // =========================================================

  const openRequestForm = (
    teacher: UserSkillSummary,
  ) => {
    if (!selectedSkill) {
      return;
    }

    setSelectedTeacher(teacher);

    setRequestMessage(
      `I want to learn ${selectedSkill.name} from you.`,
    );

    setOfferedSkillName("");

    setError("");
    setSuccess("");
  };

  // =========================================================
  // SEND REQUEST
  // =========================================================

  const sendExchangeRequest = async () => {
    if (!selectedTeacher || !selectedSkill) {
      return;
    }

    if (selectedTeacher.userId == null) {
      setError(
        "Unable to identify the selected student.",
      );
      return;
    }

    try {
      setSendingRequest(true);

      setError("");
      setSuccess("");

      const request: CreateExchangeRequest = {
        recipientUserId:
          selectedTeacher.userId,

        exchangeType:
          offeredSkillName.trim()
            ? "SKILL_SWAP"
            : "ONE_WAY",

        requestMessage:
          requestMessage.trim() ||
          undefined,

        requestedSkillName:
          selectedSkill.name,

        offeredSkillName:
          offeredSkillName.trim() ||
          undefined,
      };

      await exchangeService.createRequest(
        request,
      );

      setSuccess(
        `Request sent successfully to ${getUserName(
          selectedTeacher,
        )}.`,
      );

      setSelectedTeacher(null);
      setRequestMessage("");
      setOfferedSkillName("");

      await loadRequests();

      setActiveTab("sent");
    } catch (err) {
      console.error(
        "Failed to send request:",
        err,
      );

      setError(
        "Unable to send exchange request. Please try again.",
      );
    } finally {
      setSendingRequest(false);
    }
  };

  // =========================================================
  // ACCEPT
  // =========================================================

  const handleAccept = async (
    requestId: number,
  ) => {
    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.acceptRequest(
        requestId,
      );

      setSuccess(
        "Exchange request accepted.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to accept request:",
        err,
      );

      setError(
        "Unable to accept this request.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // REJECT
  // =========================================================

  const handleReject = async (
    requestId: number,
  ) => {
    const confirmed =
      window.confirm(
        "Are you sure you want to reject this exchange request?",
      );

    if (!confirmed) {
      return;
    }

    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.rejectRequest(
        requestId,
      );

      setSuccess(
        "Exchange request rejected.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to reject request:",
        err,
      );

      setError(
        "Unable to reject this request.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // CANCEL
  // =========================================================

  const handleCancel = async (
    requestId: number,
  ) => {
    const confirmed =
      window.confirm(
        "Are you sure you want to cancel this request?",
      );

    if (!confirmed) {
      return;
    }

    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.cancelRequest(
        requestId,
      );

      setSuccess(
        "Exchange request cancelled.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to cancel request:",
        err,
      );

      setError(
        "Unable to cancel this request.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // COMPLETE
  // =========================================================

  const handleComplete = async (
    requestId: number,
  ) => {
    const confirmed =
      window.confirm(
        "Mark this exchange as completed?",
      );

    if (!confirmed) {
      return;
    }

    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.completeRequest(
        requestId,
      );

      setSuccess(
        "Exchange marked as completed.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to complete exchange:",
        err,
      );

      setError(
        "Unable to complete this exchange.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // ARCHIVE
  // =========================================================

  const handleArchive = async (
    requestId: number,
  ) => {
    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.archiveRequest(
        requestId,
      );

      setSuccess(
        "Exchange request archived.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to archive request:",
        err,
      );

      setError(
        "Unable to archive this request.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // DELETE
  // =========================================================

  const handleDelete = async (
    requestId: number,
  ) => {
    const confirmed =
      window.confirm(
        "Delete this exchange request?",
      );

    if (!confirmed) {
      return;
    }

    try {
      setActionLoading(requestId);
      setError("");
      setSuccess("");

      await exchangeService.deleteRequest(
        requestId,
      );

      setSuccess(
        "Exchange request deleted.",
      );

      await loadRequests();
    } catch (err) {
      console.error(
        "Failed to delete request:",
        err,
      );

      setError(
        "Unable to delete this request.",
      );
    } finally {
      setActionLoading(null);
    }
  };

  // =========================================================
  // HELPERS
  // =========================================================

  const getUserName = (
    user: {
      firstName: string | null;
      lastName: string | null;
    },
  ) => {
    const name = [
      user.firstName,
      user.lastName,
    ]
      .filter(Boolean)
      .join(" ")
      .trim();

    return name || "SkillSwap Student";
  };

  const formatDate = (
    value?: string | null,
  ) => {
    if (!value) {
      return "—";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return "—";
    }

    return date.toLocaleString();
  };

  const formatStatus = (
    status: string,
  ) => {
    return status
      .replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) =>
        letter.toUpperCase(),
      );
  };

  const getStatusClass = (
    status: string,
  ) => {
    switch (status) {
      case "PENDING":
        return "bg-amber-50 text-amber-700";

      case "ACCEPTED":
        return "bg-blue-50 text-blue-700";

      case "COMPLETED":
        return "bg-emerald-50 text-emerald-700";

      case "REJECTED":
        return "bg-red-50 text-red-700";

      case "CANCELLED":
        return "bg-slate-100 text-slate-600";

      case "EXPIRED":
        return "bg-orange-50 text-orange-700";

      case "ARCHIVED":
        return "bg-slate-100 text-slate-500";

      default:
        return "bg-slate-100 text-slate-600";
    }
  };

  const getRequestPersonName = (
  request: ExchangeRequestResponse,
  incoming: boolean,
) => {
  if (incoming) {
    return (
      request.requesterName ||
      request.requesterEmail ||
      "SkillSwap Student"
    );
  }

  return (
    request.recipientName ||
    request.recipientEmail ||
    "SkillSwap Student"
  );
};

  // =========================================================
  // REQUEST CARD
  // =========================================================

  const renderRequestCard = (
    request: ExchangeRequestResponse,
    incoming: boolean,
  ) => {
    const isLoading =
      actionLoading === request.id;

    const personName =
      getRequestPersonName(
        request,
        incoming,
      );

    return (
      <article
        key={request.id}
        className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
      >
        {/* TOP */}

        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-indigo-600">
              {incoming
                ? "From student"
                : "To student"}
            </p>

            <h3 className="mt-1 text-lg font-bold text-slate-900">
              {personName}
            </h3>

            <p className="mt-1 text-sm text-slate-500">
              {request.exchangeType
                .replaceAll("_", " ")}
            </p>
          </div>

          <span
            className={`inline-flex w-fit rounded-full px-3 py-1 text-xs font-semibold ${getStatusClass(
              request.status,
            )}`}
          >
            {formatStatus(
              request.status,
            )}
          </span>
        </div>

        {/* SKILLS */}

        <div className="mt-5 grid gap-3 sm:grid-cols-2">
          <div className="rounded-xl bg-indigo-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-indigo-600">
              Requested Skill
            </p>

            <p className="mt-1 font-semibold text-slate-900">
              {request.requestedSkillName ||
                "Not specified"}
            </p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
              Offered Skill
            </p>

            <p className="mt-1 font-semibold text-slate-900">
              {request.offeredSkillName ||
                "Not specified"}
            </p>
          </div>
        </div>

        {/* MESSAGE */}

        {request.requestMessage && (
          <div className="mt-4 rounded-xl border border-slate-200 bg-white p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
              Message
            </p>

            <p className="mt-2 text-sm leading-6 text-slate-700">
              {request.requestMessage}
            </p>
          </div>
        )}

        {/* DATE */}

        <div className="mt-4 text-xs text-slate-400">
          Created:{" "}
          {formatDate(
            request.createdAt,
          )}
        </div>

        {/* ACTIONS */}

        <div className="mt-5 flex flex-wrap gap-3">

          {/* RECEIVED PENDING */}

          {incoming &&
            request.status ===
              "PENDING" && (
              <>
                <button
                  type="button"
                  disabled={isLoading}
                  onClick={() =>
                    handleAccept(
                      request.id,
                    )
                  }
                  className="rounded-xl bg-emerald-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isLoading
                    ? "Processing..."
                    : "Accept"}
                </button>

                <button
                  type="button"
                  disabled={isLoading}
                  onClick={() =>
                    handleReject(
                      request.id,
                    )
                  }
                  className="rounded-xl border border-red-200 bg-white px-4 py-2.5 text-sm font-semibold text-red-600 hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Reject
                </button>
              </>
            )}

          {/* SENT PENDING */}

          {!incoming &&
            request.status ===
              "PENDING" && (
              <button
                type="button"
                disabled={isLoading}
                onClick={() =>
                  handleCancel(
                    request.id,
                  )
                }
                className="rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Cancel Request
              </button>
            )}

          {/* ACCEPTED */}

          {request.status ===
            "ACCEPTED" && (
            <button
              type="button"
              disabled={isLoading}
              onClick={() =>
                handleComplete(
                  request.id,
                )
              }
              className="rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isLoading
                ? "Processing..."
                : "Mark Completed"}
            </button>
          )}

          {/* CLOSED */}

          {(request.status ===
            "COMPLETED" ||
            request.status ===
              "REJECTED" ||
            request.status ===
              "CANCELLED" ||
            request.status ===
              "EXPIRED") && (
            <>
              <button
                type="button"
                disabled={isLoading}
                onClick={() =>
                  handleArchive(
                    request.id,
                  )
                }
                className="rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Archive
              </button>

              <button
                type="button"
                disabled={isLoading}
                onClick={() =>
                  handleDelete(
                    request.id,
                  )
                }
                className="rounded-xl border border-red-200 bg-white px-4 py-2.5 text-sm font-semibold text-red-600 hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Delete
              </button>
            </>
          )}
        </div>
      </article>
    );
  };

  // =========================================================
  // DISCOVERY UI
  // =========================================================

  const renderDiscovery = () => {
    return (
      <>
        {/* SEARCH */}

        <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
          <label
            htmlFor="skill-search"
            className="block text-sm font-semibold text-slate-900"
          >
            What do you want to learn?
          </label>

          <div className="relative mt-3">
            <input
              id="skill-search"
              type="text"
              value={query}
              onChange={(event) => {
                setQuery(
                  event.target.value,
                );

                setSelectedSkill(null);
                setTeachers([]);
                setSuccess("");
                setError("");
              }}
              placeholder="Search Java, React, Python..."
              className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
            />

            {searchLoading && (
              <div className="absolute right-4 top-1/2 h-5 w-5 -translate-y-1/2 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />
            )}
          </div>

          {/* SKILL RESULTS */}

          {skills.length > 0 &&
            !selectedSkill && (
              <div className="mt-3 overflow-hidden rounded-xl border border-slate-200">
                {skills.map((skill) => (
                  <button
                    key={skill.id}
                    type="button"
                    onClick={() =>
                      findTeachers(
                        skill,
                      )
                    }
                    className="flex w-full items-center justify-between border-b border-slate-100 px-4 py-4 text-left last:border-0 hover:bg-indigo-50"
                  >
                    <div>
                      <p className="font-semibold text-slate-900">
                        {skill.name}
                      </p>

                      <p className="mt-1 text-xs text-slate-500">
                        {skill.category}
                      </p>
                    </div>

                    <span className="text-sm font-semibold text-indigo-600">
                      Find teachers →
                    </span>
                  </button>
                ))}
              </div>
            )}
        </section>

        {/* SELECTED SKILL */}

        {selectedSkill && (
          <section className="mt-6">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
              <div>
                <p className="text-sm text-slate-500">
                  Students who can teach
                </p>

                <h2 className="mt-1 text-2xl font-bold text-slate-900">
                  {selectedSkill.name}
                </h2>
              </div>

              <button
                type="button"
                onClick={() => {
                  setSelectedSkill(null);
                  setTeachers([]);
                  setQuery("");
                }}
                className="text-sm font-semibold text-indigo-600"
              >
                ← Search another skill
              </button>
            </div>

            {teachersLoading && (
              <div className="mt-6 rounded-2xl border border-slate-200 bg-white p-10 text-center">
                <div className="mx-auto h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

                <p className="mt-4 text-sm text-slate-500">
                  Finding teachers...
                </p>
              </div>
            )}

            {!teachersLoading &&
              teachers.length === 0 && (
                <div className="mt-6 rounded-2xl border border-slate-200 bg-white p-10 text-center">
                  <h3 className="font-semibold text-slate-900">
                    No teachers found
                  </h3>

                  <p className="mt-2 text-sm text-slate-500">
                    No active student is currently
                    available to teach this skill.
                  </p>
                </div>
              )}

            {!teachersLoading &&
              teachers.length > 0 && (
                <div className="mt-6 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
                  {teachers.map(
                    (teacher) => (
                      <article
                        key={teacher.id}
                        className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
                      >
                        <div className="flex items-start gap-4">
                          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-lg font-bold text-indigo-700">
                            {(
                              teacher.firstName?.[0] ||
                              teacher.lastName?.[0] ||
                              "S"
                            ).toUpperCase()}
                          </div>

                          <div>
                            <h3 className="font-semibold text-slate-900">
                              {getUserName(
                                teacher,
                              )}
                            </h3>

                            <p className="mt-1 text-sm text-indigo-600">
                              {
                                teacher.proficiencyLevel
                              }
                            </p>
                          </div>
                        </div>

                        <div className="mt-5 space-y-3 text-sm">
                          <div className="flex justify-between">
                            <span className="text-slate-500">
                              Experience
                            </span>

                            <span className="font-medium">
                              {
                                teacher.yearsOfExperience
                              }{" "}
                              years
                            </span>
                          </div>

                          <div className="flex justify-between">
                            <span className="text-slate-500">
                              Rating
                            </span>

                            <span className="font-medium">
                              {teacher.ratingCount >
                              0
                                ? `⭐ ${teacher.averageRating.toFixed(
                                    1,
                                  )}`
                                : "No ratings"}
                            </span>
                          </div>

                          <div className="flex justify-between">
                            <span className="text-slate-500">
                              Verified
                            </span>

                            <span className="font-medium text-emerald-600">
                              {teacher.verified
                                ? "✓ Yes"
                                : "No"}
                            </span>
                          </div>
                        </div>

                        <button
                          type="button"
                          onClick={() =>
                            openRequestForm(
                              teacher,
                            )
                          }
                          className="mt-6 w-full rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white hover:bg-indigo-700"
                        >
                          Request Exchange
                        </button>
                      </article>
                    ),
                  )}
                </div>
              )}
          </section>
        )}
      </>
    );
  };

  // =========================================================
  // REQUEST LIST
  // =========================================================

  const renderRequestList = (
    incoming: boolean,
  ) => {
    const requests = incoming
      ? receivedRequests
      : sentRequests;

    if (requestsLoading) {
      return (
        <div className="rounded-2xl border border-slate-200 bg-white p-10 text-center">
          <div className="mx-auto h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />

          <p className="mt-4 text-sm text-slate-500">
            Loading requests...
          </p>
        </div>
      );
    }

    if (requests.length === 0) {
      return (
        <div className="rounded-2xl border border-slate-200 bg-white p-10 text-center">
          <h3 className="font-semibold text-slate-900">
            No requests
          </h3>

          <p className="mt-2 text-sm text-slate-500">
            {incoming
              ? "You don't have any received exchange requests."
              : "You haven't sent any exchange requests."}
          </p>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        {requests.map((request) =>
          renderRequestCard(
            request,
            incoming,
          ),
        )}
      </div>
    );
  };

  // =========================================================
  // MAIN UI
  // =========================================================

  return (
    <AppShell>
      <div className="min-h-full bg-slate-50">
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">

          {/* HEADER */}

          <header className="border-b border-slate-200 pb-6">
            <p className="text-sm font-semibold uppercase tracking-wide text-indigo-600">
              Skill Exchange
            </p>

            <h1 className="mt-2 text-2xl font-bold text-slate-900 sm:text-3xl">
              Learn from students. Share what you know.
            </h1>

            <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
              Find students who can teach the skills
              you want to learn and exchange knowledge
              with them.
            </p>
          </header>

          {/* ALERTS */}

          {error && (
            <div className="mt-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          {success && (
            <div className="mt-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
              {success}
            </div>
          )}

          {/* TABS */}

          <div className="mt-6 flex flex-wrap gap-2 rounded-xl border border-slate-200 bg-white p-2 shadow-sm">
            <button
              type="button"
              onClick={() =>
                setActiveTab("discover")
              }
              className={`rounded-lg px-4 py-2.5 text-sm font-semibold ${
                activeTab === "discover"
                  ? "bg-indigo-600 text-white"
                  : "text-slate-600 hover:bg-slate-100"
              }`}
            >
              Find Students
            </button>

            <button
              type="button"
              onClick={() =>
                setActiveTab("sent")
              }
              className={`rounded-lg px-4 py-2.5 text-sm font-semibold ${
                activeTab === "sent"
                  ? "bg-indigo-600 text-white"
                  : "text-slate-600 hover:bg-slate-100"
              }`}
            >
              Sent Requests
              {sentRequests.filter(
                (request) =>
                  request.status ===
                  "PENDING",
              ).length > 0 && (
                <span className="ml-2 rounded-full bg-white/20 px-2 py-0.5 text-xs">
                  {
                    sentRequests.filter(
                      (request) =>
                        request.status ===
                        "PENDING",
                    ).length
                  }
                </span>
              )}
            </button>

            <button
              type="button"
              onClick={() =>
                setActiveTab("received")
              }
              className={`rounded-lg px-4 py-2.5 text-sm font-semibold ${
                activeTab === "received"
                  ? "bg-indigo-600 text-white"
                  : "text-slate-600 hover:bg-slate-100"
              }`}
            >
              Received Requests
              {receivedRequests.filter(
                (request) =>
                  request.status ===
                  "PENDING",
              ).length > 0 && (
                <span className="ml-2 rounded-full bg-white/20 px-2 py-0.5 text-xs">
                  {
                    receivedRequests.filter(
                      (request) =>
                        request.status ===
                        "PENDING",
                    ).length
                  }
                </span>
              )}
            </button>

            <button
              type="button"
              onClick={loadRequests}
              disabled={requestsLoading}
              className="ml-auto rounded-lg border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-600 hover:bg-slate-50 disabled:opacity-50"
            >
              ↻ Refresh
            </button>
          </div>

          {/* CONTENT */}

          <main className="mt-6">
            {activeTab === "discover" &&
              renderDiscovery()}

            {activeTab === "sent" &&
              renderRequestList(false)}

            {activeTab === "received" &&
              renderRequestList(true)}
          </main>
        </div>
      </div>

      {/* =====================================================
          REQUEST MODAL
      ====================================================== */}

      {selectedTeacher &&
        selectedSkill && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 px-4 py-6">
            <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl bg-white shadow-xl">

              <div className="border-b border-slate-200 px-6 py-5">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="text-sm font-medium text-indigo-600">
                      Exchange Request
                    </p>

                    <h2 className="mt-1 text-xl font-bold text-slate-900">
                      Learn{" "}
                      {selectedSkill.name}
                    </h2>

                    <p className="mt-1 text-sm text-slate-500">
                      Requesting from{" "}
                      {getUserName(
                        selectedTeacher,
                      )}
                    </p>
                  </div>

                  <button
                    type="button"
                    onClick={() =>
                      setSelectedTeacher(
                        null,
                      )
                    }
                    className="text-2xl leading-none text-slate-400 hover:text-slate-700"
                  >
                    ×
                  </button>
                </div>
              </div>

              <div className="space-y-5 px-6 py-6">

                {/* LEARN */}

                <div className="rounded-xl bg-indigo-50 p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-indigo-600">
                    You want to learn
                  </p>

                  <p className="mt-1 text-lg font-bold text-slate-900">
                    {selectedSkill.name}
                  </p>
                </div>

                {/* OFFER */}

                <div>
                  <label
                    htmlFor="offered-skill"
                    className="block text-sm font-semibold text-slate-900"
                  >
                    What can you offer?
                  </label>

                  <input
                    id="offered-skill"
                    type="text"
                    value={offeredSkillName}
                    onChange={(event) =>
                      setOfferedSkillName(
                        event.target.value,
                      )
                    }
                    placeholder="e.g. React, Python, UI/UX"
                    className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                  />

                  <p className="mt-1 text-xs text-slate-500">
                    Leave empty for a one-way learning
                    request.
                  </p>
                </div>

                {/* MESSAGE */}

                <div>
                  <label
                    htmlFor="request-message"
                    className="block text-sm font-semibold text-slate-900"
                  >
                    Message
                  </label>

                  <textarea
                    id="request-message"
                    rows={4}
                    maxLength={2000}
                    value={requestMessage}
                    onChange={(event) =>
                      setRequestMessage(
                        event.target.value,
                      )
                    }
                    className="mt-2 w-full resize-none rounded-xl border border-slate-300 px-4 py-3 text-sm outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                  />

                  <p className="mt-1 text-right text-xs text-slate-400">
                    {requestMessage.length}/2000
                  </p>
                </div>

                {/* BUTTONS */}

                <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                  <button
                    type="button"
                    disabled={
                      sendingRequest
                    }
                    onClick={() =>
                      setSelectedTeacher(
                        null,
                      )
                    }
                    className="rounded-xl border border-slate-300 px-5 py-3 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                  >
                    Cancel
                  </button>

                  <button
                    type="button"
                    disabled={
                      sendingRequest
                    }
                    onClick={
                      sendExchangeRequest
                    }
                    className="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white hover:bg-indigo-700 disabled:opacity-50"
                  >
                    {sendingRequest
                      ? "Sending..."
                      : "Send Exchange Request"}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
    </AppShell>
  );
};

export default ExchangePage;