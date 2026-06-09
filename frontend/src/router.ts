import { createRouter, createWebHistory } from 'vue-router'
import HomeView from './pages/Home.vue'
import ConstituenciesView from './pages/Constituencies.vue'
import MunicipalitiesView from "./pages/Municipality.vue";
import compareMunicipalitiesView from "./pages/CompareMunicipalities.vue";
import PartiesView from './pages/Parties.vue'
import PartyCandidatesView from './pages/PartyCandidates.vue'
import NationalResultsView from './pages/NationalResults.vue'
import GraphConstituencies from "./pages/GraphConstituencies.vue";
import MapResultsView from './pages/MapResults.vue'
import ForumPage from "./pages/ForumPage.vue";
import CreatePost from "./pages/CreatePost.vue";
import ViewPost from "./pages/ViewPost.vue";
import ForumAdmin from "./pages/ForumAdmin.vue";
import logInView from "./pages/LogIn.vue";
import RegisterView from "./pages/Register.vue";
import ProfileView from "./pages/Profile.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/constituencies',
            name: 'constituencies',
            component: ConstituenciesView,
        },
        {
            path: '/national-results',
            name: 'national-results',
            component: NationalResultsView,
        },

        {
            path: '/map-results',
            name: 'map-results',
            component: MapResultsView,
        },

        {
            path: '/compareConstituency',
            name: 'GraphConstituencies',
            component: GraphConstituencies,
        },

        {
            path: '/parties',
            name: 'parties',
            component: PartiesView,
        },

        {
            path: '/municipalities',
            name: 'municipalities',
            component: MunicipalitiesView,
        },

        {
            path: '/logIn',
            name: 'logIn',
            component: logInView,
        },

        {
            path: '/register',
            name: 'register',
            component: RegisterView,
        },

        {
            path: '/profile',
            name: 'profile',
            component: ProfileView,
        },


        {
            path: '/compareMunicipalities',
            name: 'compareMunicipalities',
            component: compareMunicipalitiesView,
        },
        {
            path: '/ForumPage',
            name: 'Formpage',
            component: ForumPage,
        },
        {
            path: '/createPost',
            name: 'CreatePost',
            component: CreatePost,
        },
        {
            path: '/forum-admin',
            name: 'forum-admin',
            component: ForumAdmin,
        },

        {
            path: '/parties/:partyName/candidates',
            name: 'party-candidates',
            component: PartyCandidatesView,
            props: true,
        },
        {
            path: '/forumPost/:id',
            name: 'forumPost',
            component: ViewPost
        }

    ],
})

router.afterEach(() => {
    // Ensure keyboard users land in the new page content after navigation.
    // Each page should expose a focusable main region: <main id="main-content" tabindex="-1">.
    requestAnimationFrame(() => {
        const main = document.getElementById('main-content') as HTMLElement | null
        if (!main) return

        // preventScroll is not supported everywhere, but harmless where it is.
        try {
            main.focus({ preventScroll: true } as any)
        } catch {
            main.focus()
        }
    })
})

export default router
