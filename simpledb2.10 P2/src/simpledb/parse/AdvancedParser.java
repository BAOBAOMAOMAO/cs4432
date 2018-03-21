package simpledb.parse;

public class AdvancedParser extends Parser
{
	Lexer lex;
	public AdvancedParser(String s) {
		super(s);
		// TODO Auto-generated constructor stub
		this.lex = this.getLex();
	}
	
	@Override
	public CreateIndexData createIndex() 
	{
		String idxtype = lex.eatId();
		lex.eatKeyword("index");
		String idxname = lex.eatId();
		lex.eatKeyword("on");
		String tblname = lex.eatId();
		lex.eatDelim('(');
		String fldname = field();
		lex.eatDelim(')');
		return new CreateIndexData(idxtype, idxname, tblname, fldname);
	}
}
