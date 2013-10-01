

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalUserServiceTestConfig())
            .setEnvIsAdmin(true).setEnvIsLoggedIn(true);

  @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testIsAdmin() {
        UserService userService = UserServiceFactory.getUserService();
        assertTrue(userService.isUserAdmin());
    }
