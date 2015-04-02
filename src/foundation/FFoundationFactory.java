package foundation;

/**
 * All class methods works only if there exists a pair of class <mclass,fclass> such that
 * mclass belongs to the Model package, fclass belongs to th Foundation package and 
 * the names of the two classes differs only for the first letter.
 * @author fausto
 */
public class FFoundationFactory {

	protected FFoundationAbstract ffoundation;
	
	public FFoundationFactory() {
		this.ffoundation = null;
	}
	
	protected FFoundationAbstract getFFoundation(String mclass)
	{
		String fclass = this.generateGeneralFClassString(mclass);
		return this.getFFoundation(fclass, mclass);
	}
	
	@SuppressWarnings("rawtypes")
	protected FFoundationAbstract getFFoundation(Class mclass)
	{		
		String mclassStr = mclass.getSimpleName();;
		return this.getFFoundation(mclassStr);
	}
	
	protected FFoundationAbstract getFFoundation(Object modelObj)
	{	
		String mclass = modelObj.getClass().getSimpleName();
		return this.getFFoundation(mclass);
	}
	
	protected String generateGeneralFClassString(String mclass) {
		String fclass = "foundation.F" + mclass.substring(1);
		return fclass;
	}
	
	protected FFoundationAbstract getFFoundation(String fclass,String mclass)
	{		
		return this.getGeneralFactory(fclass, mclass);
	}
	
	protected FFoundationAbstract getGeneralFactory(String fclass,String mclass)
//	protected FFoundationAbstract getFFoundation(String fclass,String mclass)
	{	
		try {
			ffoundation = (FFoundationAbstract) Class.forName(fclass).newInstance();
		} catch (InstantiationException e) {
			System.out.print("Unable to get an instance of the class " + mclass + "\n"
					+fclass + " do not exists \n");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.print("Unable to access the class " + mclass + "\n"
					+fclass + " do not exists \n");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.print("Unable to load elements of the class " + mclass + "\n"
					+fclass + " do not exists \n");
			e.printStackTrace();
		}
		return ffoundation;
	}
}
