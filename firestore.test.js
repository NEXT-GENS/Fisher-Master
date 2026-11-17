const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfile(uid) {
  const now = new Date(Date.now() - 1000);
  return {
    userId: uid,
    username: "Nelayan_Pro",
    avatarId: "avatar_marlin",
    level: 3,
    xp: 240,
    coins: 1500,
    gems: 25,
    totalCatches: 12,
    biggestCatchWeightKg: 14.5,
    biggestCatchFishName: "Marlin Biru",
    equippedRodId: "rod_bamboo",
    equippedReelId: "reel_basic",
    equippedLineId: "line_nylon",
    equippedBaitId: "bait_worm",
    equippedSkinId: "skin_classic",
    activeLocationId: "danau_tenang",
    loginStreakDay: 1,
    lastLoginDate: "2026-11-15",
    battlePassXp: 120,
    isPremiumPass: false,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write profiles, leaderboard, or rooms", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("leaderboard").where("isPublic", "==", true).get()
  );
  await assertFails(
    unauthDb.collection("rooms").where("status", "in", ["waiting", "playing"]).get()
  );
});

test("Authenticated user can create and read own profile, but not another user's", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Shadow update with ghost field on profile is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const badProfile = { ...validProfile(ALICE_UID), isAdmin: true };
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set(badProfile)
  );
});

test("Authenticated user can write catch and gear in own subcollections only", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  const catchPayload = {
    userId: ALICE_UID,
    fishId: "fish_arwana",
    fishName: "Arwana Emas",
    rarity: "Legendary",
    weightKg: 8.4,
    lengthCm: 92.0,
    locationId: "sungai_hijau",
    weather: "Hujan",
    sellPrice: 1200,
    isKept: true,
    isRecord: true,
    caughtAt: now,
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("catches").doc("catch_1").set(catchPayload)
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("catches").get()
  );
});

test("Leaderboard enforces ownership on write and public filter on list", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: ALICE_UID,
      username: "Alice",
      avatarId: "avatar_marlin",
      level: 5,
      biggestFishName: "Marlin",
      biggestWeightKg: 42.0,
      tournamentScore: 800,
      totalCatches: 25,
      isPublic: true,
      updatedAt: now,
    })
  );

  // Bob cannot overwrite Alice's leaderboard entry
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: BOB_UID,
      username: "Bob",
      avatarId: "avatar_marlin",
      level: 5,
      biggestFishName: "Hacked",
      biggestWeightKg: 999.0,
      tournamentScore: 9999,
      totalCatches: 99,
      isPublic: true,
      updatedAt: now,
    })
  );

  // Bob can query public leaderboard
  await assertSucceeds(
    bobDb.collection("leaderboard").where("isPublic", "==", true).get()
  );

  // Unfiltered list fails
  await assertFails(bobDb.collection("leaderboard").get());
});

test("Multiplayer rooms allow creation, joining, chat messages, and reject unfiltered list", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("rooms").doc("room_1").set({
      hostId: ALICE_UID,
      roomCode: "FM8821",
      name: "Turnamen Laut Biru",
      region: "Indonesia",
      locationId: "laut_biru",
      status: "waiting",
      maxPlayers: 6,
      playerCount: 1,
      participantIds: [ALICE_UID],
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    bobDb.collection("rooms").where("status", "in", ["waiting", "playing"]).get()
  );
  await assertFails(bobDb.collection("rooms").get());

  await assertSucceeds(
    bobDb.collection("rooms").doc("room_1").update({
      participantIds: [ALICE_UID, BOB_UID],
      playerCount: 2,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    bobDb.collection("rooms").doc("room_1").collection("messages").doc("msg_1").set({
      roomId: "room_1",
      senderId: BOB_UID,
      senderName: "Bob",
      text: "Strike ikan besar!",
      type: "chat",
      createdAt: now,
    })
  );
});
