package bmird.radboud.fieldtripclientsservice;

/**
 * Class containing all the constants of this project.
 *
 * @author wieke
 *
 */
public final class C {
	public static final String FILTER_FOR_CLIENTS = "bmird.radboud.fieldtripclientsservice.clientsfilter";
    public static final String SEND_UPDATEINFO_TO_CONTROLLER_ACTION = "bmird.radboud.fieldtripbufferservicecontroller.action.UPDATEINFO";

	public static final String MESSAGE_TYPE = "a";
	public static final String THREAD_INDEX = "t_index";
	public static final String THREAD_ARGUMENTS = "t_args";
    public static final String THREAD_STRING_FOR_ARG = "t_arg_str";
	public static final String THREAD_ID = "t_id";
	public static final String THREAD_N_ARGUMENTS = "t_nArgs";
    public static final String IS_THREAD_INFO = "t_Info";
    public static final String THREAD_INFO = "t";

    public static final int THREAD_INFO_TYPE = 0;
	public static final int UPDATE = 1;
	public static final int THREAD_STOP = 6;
	public static final int THREAD_PAUSE = 7;
	public static final int THREAD_START = 8;
    public static final int THREAD_UPDATE_ARGUMENTS = 9;
    public static final int THREAD_UPDATE_ARG_FROM_STR = 10;

	public static final String TAG = "fieldtripthreads";

	public static final String WAKELOCKTAG = "bmird.radboud.fieldtripclientsservice.wakelock";

	public static final String WAKELOCKTAGWIFI = "bmird.radboud.fieldtripclientsservice.wakelockwifi";

}
