# ADR 008: Screen Architecture (v3)

## Status
Accepted (Supersedes [001](./001-screen-controllers.md), [007](./007-screen-architecture-v2.md)) on 2019-10-01

## Context
Our [intermediate architecture](./007-screen-architecture-v2.md) was a step above the [v1 screen architecture](./001-screen-controllers.md). However, it still has problems of its own:

- The `UiStateProducer` is still some sort of a **God Class** since it is reponsible for both business logic (what needs to happen) and effects (changes to the system). This makes testing it cumbersome since the tests are more of integration tests which end up verifying both business logic and implementation details.
- The architecture is heavily dependent on [RxJava](https://github.com/ReactiveX/RxJava/). While `RxJava` is good for a lot of things, the current architecture encourages us to use it for everything and this eventually leads to the tests and production code becoming increasingly harder to maintain and refactor.

We need a scalable framework to build screens that lets us manage business logic and effects, and which is receptive to change.

## Decision

### Goals
- Separate business logic, presentation logic, and effects so that they can tested independently of each other.
- Make UI state explicit and save/restore it manually instead of depending on hidden behaviour.
- Separate business logic and effects.
- Restrict RxJava to managing events and effects, and let the business logic be implemented as pure functions.
- Separate tests for business logic and effects so that we can manage them more easily.
- Have a way to migrate from the v1 architecture to the v3 for existing screens.

### Choosing a framework
We evaluated many patterns and frameworks that are common in the industry, including but not limited to:

##### [Android recommended architecture](https://developer.android.com/jetpack/docs/guide)
The Android recommended architecture depends on a few architecture components to work together:

- [`ViewModel`](https://developer.android.com/topic/libraries/architecture/viewmodel): To retain the state across configuration changes.
- [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData): To provide lifecycle-aware reactive notifications to the UI.
- [`Lifecyle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle): To automatically manage subscriptions to the reactive notifications from the `LiveData` instances provided via the `ViewModel`.

This architecture makes sense for new codebases, but this is a codebase with an already existing architecture which does not lend itself way to this specific setup. Some of the problems are:

- The `Lifecycle` component is designed to work with screens that are built on top `Activity` or `Fragment` classes. However, our current architecture uses a single `Activity` setup, where the individual screens are implemented using `View` subclasses. The `LiveData` component will not give us much benefit unless we transition all the `View` based screens to be `Fragment` instances.
- We already use `RxJava` for reactive notifications. Using `LiveData` would mean that we would either have to replace usages of `RxJava` with `LiveData` which is neither feasible not desirable at this point.

##### [MvRx, by Airbnb](https://github.com/airbnb/MvRx)
`MvRx` is a library that is built on top of `RxJava` and the Android `ViewModel` architecture component. While this is a good architecture, it has a couple of limitations which stopped us from choosing this:

- Its core model is based on `RxJava`. We already have issues because of an overuse of `RxJava` across the app and part of the goals of this new architecture is to restrict the usage of `RxJava` to limited sections of the codebase.
- It does not support custom views and is designed to be used with `Fragment` based screens. This suffers from the same problem as the Android recommended architecture.

##### [MVI](http://hannesdorfmann.com/android/mosby3-mvi-1)
`MVI` (Model-View-Intent) was one of the more promising architectures that we reviewed. The problem with `MVI` however, is that most implementations of `MVI` in the industry are implemented differently based on the needs of the project. There is no "one-way" to implement it at all. Instead, we took a look at the core principles which most `MVI` implementations are based on, which are more similar to [`Redux`](https://redux.js.org/introduction/three-principles).

- Single source of truth
- State is read-only
- Changes are made with pure functions

Looking at the frameworks available in the Android world that are built on these principles, we found [Mobius](https://github.com/spotify/mobius), a reactive framework for managing state and side-effects by Spotify.

The [objectives](https://github.com/spotify/mobius/wiki/Objectives) of the framework also aligned very well with what we need from the architecture. Thus, we decided to use it as the basis for the v3 screen architecture. 

See [this page](../mobius/implementing-a-new-screen.md) for a detailed description of what it looks like to implement a new screen in Mobius.

#### Reference implementation
A reference implementation of the complete architecture can be found at [this commit](https://github.com/simpledotorg/simple-android/tree/e51a6b2fad295f9da4064b95f51d95e3ab2f0e6c/app/src/main/java/org/simple/clinic/settings/changelanguage).

### Goals Review

#### Fulfill all the requirements of the [v2 architecture](./007-screen-architecture-v2.md#goals)

- **Separation of presentation and business logic:** This is satisfied by Mobius since it enables us to separate concerns at an even more granular level than before.
- **Making UI state explicit and support state restoration:** This is handled by the `MobiusDelegate` classes for us and enforced by it since the class expects the `Model` to be `Parcelable` by default.

#### Separation of concerns and testability
We have three core components (`Update`, `EffectHandler`, and `UiRenderer`), which are responsible for discrete parts of the system. These are smaller and more focused, thereby making them easier to test and maintain as well.

#### Migrating from the older architecture
Unlike the [v2 architecture](./007-screen-architecture-v2.md#migrating-from-the-older-architecture), the v3 architecture is a much larger change from what we used to do earlier. As a consequence, migrating to the newer architecture is a much more involved process that requires us to follow deliberate, measured steps. The migration process is out of scope for this document, and is detailed in [this document](../migrating-to-mobius.md)

## Consequences
- In legacy controllers, events were cached and replayed as soon as the screen was inflated without waiting for it to be attached to the view hierarchy. In the v3 architecture, any events which are forwarded to the binding before the screen is attached to the view hierarchy, will be lost and ignored. This is the correct way to implement the events, but some of the older screen controllers which were dependent on that behaviour might take more effort to migrate to the current architecture.
- Migrating to the v3 architecture from the v1 architecture is a process that takes some time to understand and get used to. Until this migration completes, new developers will need to be onboarded onto both the v1 and v3 architectures so that the codebase can be maintained.
