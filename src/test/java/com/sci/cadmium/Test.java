package com.sci.cadmium;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.sci.cadmium.config.ConfigTest;

/**
 * Cadmium
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@RunWith(Suite.class)
@SuiteClasses(
{ ConfigTest.class })
public class Test
{
}